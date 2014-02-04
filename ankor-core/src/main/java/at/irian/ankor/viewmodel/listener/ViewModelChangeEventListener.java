package at.irian.ankor.viewmodel.listener;

import at.irian.ankor.change.ChangeEvent;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.match.RefMatcher;
import at.irian.ankor.viewmodel.metadata.ChangeListenerMetadata;
import at.irian.ankor.viewmodel.metadata.InvocationMetadata;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

/**
 * @author Manfred Geiler
 */
public class ViewModelChangeEventListener extends ChangeEventListener {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnnotationChangeEventListener.class);

    private final Ref viewModelBeanRef;
    private final WeakReference<Object> beanReference;
    private final Collection<ChangeListenerMetadata> changeListenersMetadata;

    public ViewModelChangeEventListener(Ref viewModelBeanRef,
                                        Object bean,
                                        Collection<ChangeListenerMetadata> changeListenersMetadata) {
        super(null);
        this.viewModelBeanRef = viewModelBeanRef;
        this.beanReference = new WeakReference<Object>(bean);
        this.changeListenersMetadata = changeListenersMetadata;
    }

    @Override
    public void process(ChangeEvent event) {
        Object bean = beanReference.get();
        if (bean != null && viewModelBeanRef.isValid()) {
            Ref changedProperty = event.getChangedProperty();
            for (ChangeListenerMetadata changeListenerMetadata : changeListenersMetadata) {
                RefMatcher.Result match = changeListenerMetadata.getPattern().match(changedProperty, viewModelBeanRef);
                if (match.isMatch()) {
                    InvocationMetadata invocation = changeListenerMetadata.getInvocation();
                    invoke(bean, invocation, match.getBackRefs());
                }
            }
        }
    }

    private void invoke(Object bean, InvocationMetadata invocation, List<Ref> backRefs) {
        Method method = invocation.getMethod();
        try {
            method.invoke(bean, backRefs.toArray());
        } catch (Exception e) {
            throw new RuntimeException(String.format("Error invoking change listener method %s on view model object %s",
                                                     method,
                                                     bean), e);
        }
    }

    @Override
    public boolean isDiscardable() {
        return beanReference.get() == null || !viewModelBeanRef.isValid();
    }

}
