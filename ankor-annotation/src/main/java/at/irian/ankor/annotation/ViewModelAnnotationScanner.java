package at.irian.ankor.annotation;

import at.irian.ankor.action.Action;
import at.irian.ankor.model.ViewModelBase;
import at.irian.ankor.model.ViewModelPostProcessor;
import at.irian.ankor.path.PathSyntax;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.listener.RefActionListener;
import at.irian.ankor.ref.listener.RefChangeListener;
import at.irian.ankor.ref.match.RefMatcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static at.irian.ankor.ref.listener.RefListeners.addChangeListener;
import static at.irian.ankor.ref.listener.RefListeners.addPropActionListener;

/**
 * @author Manfred Geiler
 */
public class ViewModelAnnotationScanner implements ViewModelPostProcessor {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ViewModelAnnotationScanner.class);

    @Override
    public void postProcess(ViewModelBase viewModelObject, Ref viewModelRef) {
        scan(viewModelObject, viewModelRef);
    }


    private void scan(ViewModelBase modelObject, Ref modelRef) {
        Class<?> modelType = modelObject.getClass();
        while (modelType != null) {
            scan(modelObject, modelRef, modelType);
            modelType = modelType.getSuperclass();
        }
    }

    private void scan(ViewModelBase modelObject, Ref modelRef, Class<?> modelType) {
        for (Method method : modelType.getDeclaredMethods()) {

            ChangeListener changeListenerAnnotation = method.getAnnotation(ChangeListener.class);
            if (changeListenerAnnotation != null) {
                addChangeListener(modelRef, new MyChangeListener(modelObject, modelRef,
                                                                 changeListenerAnnotation.pattern(),
                                                                 method));
            }

            ActionListener actionListenerAnnotation = method.getAnnotation(ActionListener.class);
            if (actionListenerAnnotation != null) {
                String actionName = actionListenerAnnotation.name();
                if (actionName == null || actionName.isEmpty()) {
                    actionName = method.getName();
                }
                String[] paramNames = new String[method.getParameterTypes().length];
                for (int i = 0; i < method.getParameterAnnotations().length; i++) {
                    Annotation[] paramAnnotations = method.getParameterAnnotations()[i];
                    for (Annotation paramAnnotation : paramAnnotations) {
                        if (paramAnnotation instanceof Param) {
                            paramNames[i] = ((Param) paramAnnotation).value();
                        }
                    }
                }
                addPropActionListener(modelRef, new MyActionListener(modelObject, actionName,
                                                                     method, paramNames));
            }
        }
    }


    private static class MyChangeListener implements RefChangeListener {

        private final ViewModelBase modelObject;
        private final Ref modelRef;
        private final String[] patterns;
        private final Method method;

        public MyChangeListener(ViewModelBase modelObject, Ref modelRef, String[] patterns, Method method) {
            this.modelObject = modelObject;
            this.modelRef = modelRef;
            this.patterns = patterns;
            this.method = method;
        }

        @Override
        public void processChange(Ref changedProperty) {
            PathSyntax pathSyntax = modelRef.context().pathSyntax();
            for (String pattern : patterns) {
                RefMatcher matcher = new RefMatcher(pathSyntax, pattern);
                RefMatcher.Result matchResult = matcher.match(changedProperty);
                if (matchResult.isMatch()) {
                    try {
                        method.invoke(modelObject, matchResult.getWatchedRefs().toArray());
                    } catch (Exception e) {
                        throw new RuntimeException(String.format("Error invoking change listener method %s on view model object %s",
                                                                 method,
                                                                 modelObject), e);
                    }
                }
            }
        }
    }


    private static class MyActionListener implements RefActionListener {

        private final ViewModelBase modelObject;
        private final String actionName;
        private final Method method;
        private final String[] paramNames;

        public MyActionListener(ViewModelBase modelObject, String actionName, Method method, String[] paramNames) {
            this.modelObject = modelObject;
            this.paramNames = paramNames;
            this.actionName = actionName;
            this.method = method;
        }

        @Override
        public void processAction(Ref sourceProperty, Action action) {
            if (action.getName().equals(actionName)) {
                try {
                    Object[] paramValues = new Object[paramNames.length];
                    for (int i = 0; i < paramNames.length; i++) {
                        paramValues[i] = action.getParams().get(paramNames[i]);
                    }
                    method.invoke(modelObject, paramValues);
                } catch (Exception e) {
                    throw new RuntimeException(String.format("Error invoking action listener method %s on view model object %s",
                                                             method,
                                                             modelObject), e);
                }
            }
        }
    }


}
