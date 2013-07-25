package at.irian.ankor.annotation;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.SimpleAction;
import at.irian.ankor.action.SimpleParamAction;
import at.irian.ankor.model.ViewModelBase;
import at.irian.ankor.path.PathSyntax;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefMatcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author Manfred Geiler
 */
public abstract class AnnotationAwareViewModelBase extends ViewModelBase {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnnotationAwareViewModelBase.class);

    protected AnnotationAwareViewModelBase(Ref viewModelRef) {
        super(viewModelRef);
        scan();
    }

    private void scan() {
        if (thisRef() == null) {
            // ignore uninitialized view model object... e.g. on javafx client reusing the server model
            return;
        }

        for (Method method : this.getClass().getDeclaredMethods()) {
            ChangeListener changeListenerAnnotation = method.getAnnotation(ChangeListener.class);
            if (changeListenerAnnotation != null) {
                thisRef().addChangeListener(new MyChangeListener(changeListenerAnnotation.pattern(), method));
            }
            ActionListener actionListenerAnnotation = method.getAnnotation(ActionListener.class);
            if (actionListenerAnnotation != null) {
                String actionName = actionListenerAnnotation.name();
                if (actionName == null || actionName.isEmpty()) {
                    actionName = method.getName();
                }
                String[] paramNames = new String[method.getParameterTypes().length];
                int idx = 0;
                for (int i = 0; i < method.getParameterAnnotations().length; i++) {
                    Annotation[] paramAnnotations = method.getParameterAnnotations()[i];
                    for (Annotation paramAnnotation : paramAnnotations) {
                        if (paramAnnotation instanceof Param) {
                            paramNames[i] = ((Param) paramAnnotation).value();
                        }
                    }
                }
                thisRef().addPropActionListener(new MyActionListener(actionName, method, paramNames));
            }
        }
    }


    private class MyChangeListener implements at.irian.ankor.ref.ChangeListener {

        private final String[] patterns;
        private final Method method;

        public MyChangeListener(String[] patterns, Method method) {
            this.patterns = patterns;
            this.method = method;
        }

        @Override
        public void processChange(Ref unusedWatchedProperty, Ref changedProperty) {
            PathSyntax pathSyntax = thisRef().context().pathSyntax();
            for (String pattern : patterns) {
                RefMatcher matcher = new RefMatcher(pathSyntax, pattern);
                RefMatcher.Result matchResult = matcher.match(changedProperty);
                if (matchResult.isMatch()) {
                    try {
                        method.invoke(AnnotationAwareViewModelBase.this, matchResult.getWatchedRefs().toArray());
                    } catch (Exception e) {
                        throw new RuntimeException(String.format("Error invoking change listener method %s on view model object %s",
                                                                 method,
                                                                 AnnotationAwareViewModelBase.this), e);
                    }
                }
            }
        }
    }


    private class MyActionListener implements at.irian.ankor.ref.ActionListener {
        private final String actionName;
        private final Method method;
        private final String[] paramNames;

        public MyActionListener(String actionName, Method method, String[] paramNames) {
            this.paramNames = paramNames;
            this.actionName = actionName;
            this.method = method;
        }

        @Override
        public void processAction(Ref sourceProperty, Action action) {
            if (action instanceof SimpleAction) {
                if (((SimpleAction)action).getName().equals(actionName)) {
                    try {
                        Object[] paramValues = new Object[paramNames.length];
                        for (int i = 0; i < paramNames.length; i++) {
                            paramValues[i] = ((SimpleParamAction) action).getParams().get(paramNames[i]);
                        }
                        method.invoke(AnnotationAwareViewModelBase.this, paramValues);
                    } catch (Exception e) {
                        throw new RuntimeException(String.format("Error invoking action listener method %s on view model object %s",
                                                                 method,
                                                                 AnnotationAwareViewModelBase.this), e);
                    }
                }
            }
        }
    }
}
