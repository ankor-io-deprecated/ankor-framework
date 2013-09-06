package at.irian.ankor.annotation;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.action.ActionEventListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.match.RefMatcher;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * @author Manfred Geiler
 */
public class AnnotationActionEventListener extends ActionEventListener {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnnotationActionEventListener.class);

    private final String actionName;
    private final Object bean;
    private final RefMatcher[] matchers;
    private final Method method;
    private final String[] paramNames;
    private final Lifeline lifeline;

    public AnnotationActionEventListener(Ref watchedProperty,
                                         String actionName,
                                         Object bean,
                                         RefMatcher[] matchers,
                                         Method method,
                                         String[] paramNames,
                                         Lifeline lifeline) {
        super(watchedProperty);
        this.actionName = actionName;
        this.bean = bean;
        this.matchers = matchers;
        this.method = method;
        this.paramNames = paramNames;
        this.lifeline = lifeline;
    }

    @Override
    public void process(ActionEvent event) {
        Action action = event.getAction();
        String eventActionName = action.getName();
        Ref actionProperty = event.getActionProperty();
        if (eventActionName.equals(actionName)) {
            if (matchers != null) {
                RefMatcher.Result match = match(actionProperty, getWatchedProperty(), matchers);
                if (match != null) {
                    invokeMethod(action, match.getBackRefs());
                }
            } else {
                invokeMethod(action, Collections.<Ref>emptyList());
            }
        }
    }

    private void invokeMethod(Action action, List<Ref> backRefs) {
        try {

            int backRefsCnt = backRefs.size();
            Object[] paramValues = new Object[backRefsCnt + paramNames.length];

            for (int i = 0; i < backRefsCnt; i++) {
                paramValues[i] = backRefs.get(i);
            }

            for (int i = 0; i < paramNames.length; i++) {
                paramValues[backRefsCnt + i] = action.getParams().get(paramNames[i]);
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
        return !lifeline.isAlive();
    }


    private static RefMatcher.Result match(Ref propertyToMatch,
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
