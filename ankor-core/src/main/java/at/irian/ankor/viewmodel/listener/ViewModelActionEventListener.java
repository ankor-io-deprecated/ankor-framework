package at.irian.ankor.viewmodel.listener;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.action.ActionEventListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.match.RefMatcher;
import at.irian.ankor.viewmodel.metadata.ActionListenerMetadata;
import at.irian.ankor.viewmodel.metadata.InvocationMetadata;
import at.irian.ankor.viewmodel.metadata.ParameterMetadata;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class ViewModelActionEventListener extends ActionEventListener {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnnotationActionEventListener.class);

    private final WeakReference<Object> beanReference;
    private final Collection<ActionListenerMetadata> actionListenersMetadata;
    private final TouchHelper touchHelper;

    public ViewModelActionEventListener(Ref watchedProperty,
                                        Object bean,
                                        Collection<ActionListenerMetadata> actionListenersMetadata) {
        super(watchedProperty);
        this.beanReference = new WeakReference<Object>(bean);
        this.actionListenersMetadata = actionListenersMetadata;
        this.touchHelper = new TouchHelper(watchedProperty);
    }

    @Override
    public void process(ActionEvent event) {
        Object bean = beanReference.get();
        Ref watchedProperty = getWatchedProperty();
        if (bean != null && watchedProperty.isValid()) {
            Action action = event.getAction();
            String eventActionName = action.getName();
            Ref actionProperty = event.getActionProperty();
            for (ActionListenerMetadata actionListenerMetadata : actionListenersMetadata) {
                if (eventActionName.equals(actionListenerMetadata.getActionName())) {
                    RefMatcher pattern = actionListenerMetadata.getPattern();
                    if (pattern != null) {
                        RefMatcher.Result match = pattern.match(actionProperty, watchedProperty);
                        if (match.isMatch()) {
                            InvocationMetadata invocation = actionListenerMetadata.getInvocation();
                            invoke(bean, invocation, match.getBackRefs(), action.getParams());
                            touchHelper.touch(invocation.getTouchedProperties());
                        }
                    } else {
                        if (watchedProperty.equals(event.getActionProperty())) {
                            InvocationMetadata invocation = actionListenerMetadata.getInvocation();
                            invoke(bean, invocation, Collections.<Ref>emptyList(), action.getParams());
                            touchHelper.touch(invocation.getTouchedProperties());
                        }
                    }
                }
            }
        }
    }

    private void invoke(Object bean,
                        InvocationMetadata invocation,
                        List<Ref> backRefs,
                        Map<String, Object> actionParams) {
        Method method = invocation.getMethod();

        try {

            ParameterMetadata[] parameterInfos = invocation.getParameters();

            Object[] paramValues = new Object[parameterInfos.length];
            int backRefIdx = 0;
            for (int i = 0; i < parameterInfos.length; i++) {
                Object v;
                if (parameterInfos[i].isBackReference()) {
                    v = backRefs.get(backRefIdx++);
                } else {
                    String paramName = parameterInfos[i].getName();
                    if (paramName == null) {
                        throw new IllegalStateException("Missing action parameter name metadata for parameter " + i + " of method " + method);
                    }
                    v = actionParams.get(paramName);
                    if (v == null && !actionParams.containsKey(paramName)) {
                        throw new IllegalArgumentException("Missing action parameter value for parameter " + paramName);
                    }
                }
                paramValues[i] = v;
            }
            method.invoke(bean, paramValues);

        } catch (Exception e) {
            throw new RuntimeException(String.format("Error invoking action listener method %s on view model object %s",
                                                     method,
                                                     bean), e);
        }
    }

    @Override
    public boolean isDiscardable() {
        return beanReference.get() == null || !getWatchedProperty().isValid();
    }

}
