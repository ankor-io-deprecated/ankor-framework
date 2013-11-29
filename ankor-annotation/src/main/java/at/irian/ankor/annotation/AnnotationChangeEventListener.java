package at.irian.ankor.annotation;

import at.irian.ankor.change.ChangeEvent;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.match.RefMatcher;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

/**
 * @author Manfred Geiler
 */
public class AnnotationChangeEventListener extends ChangeEventListener {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnnotationChangeEventListener.class);

    private final Ref beanRef;
    private final WeakReference<Object> beanReference;
    private final RefMatcher[] matchers;
    private final Method method;

    public AnnotationChangeEventListener(Ref beanRef,
                                         Object bean,
                                         RefMatcher[] matchers,
                                         Method method) {
        super(beanRef.root());
        this.beanRef = beanRef;
        this.beanReference = new WeakReference<Object>(bean);
        this.matchers = matchers;
        this.method = method;
    }

    @Override
    public void process(ChangeEvent event) {
        Object bean = beanReference.get();
        if (bean != null && beanRef.isValid()) {

            Ref changedProperty = event.getChangedProperty();

            RefMatcher.Result match = match(changedProperty, beanRef, matchers);
            if (match != null) {
                try {
                    method.invoke(bean, match.getBackRefs().toArray());
                } catch (Exception e) {
                    throw new RuntimeException(String.format("Error invoking change listener method %s on view model object %s",
                                                             method,
                                                             bean), e);
                }
            }

        }
    }

    @Override
    public boolean isDiscardable() {
        return beanReference.get() == null || !getWatchedProperty().isValid();
    }


    private RefMatcher.Result match(Ref propertyToMatch,
                                    Ref contextProperty,
                                    RefMatcher[] matchers) {
        for (RefMatcher matcher : matchers) {
            RefMatcher.Result matchResult = matcher.match(propertyToMatch, contextProperty);
            if (matchResult.isMatch()) {
                return matchResult;
            }
        }
        return null;
    }

}
