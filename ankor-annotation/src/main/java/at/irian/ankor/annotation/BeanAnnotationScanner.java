package at.irian.ankor.annotation;

import at.irian.ankor.event.EventListeners;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.impl.RefContextImplementor;
import at.irian.ankor.ref.match.RefMatcher;
import at.irian.ankor.ref.match.RefMatcherFactory;
import at.irian.ankor.ref.match.pattern.AntlrRefMatcherFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author Manfred Geiler
 */
public class BeanAnnotationScanner {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnnotationViewModelPostProcessor.class);

    private final RefMatcherFactory refMatcherFactory = new AntlrRefMatcherFactory();

    public void scan(Object bean, Ref beanRef) {
        scan(bean, beanRef, getEventListeners(beanRef));
    }

    private EventListeners getEventListeners(Ref beanRef) {
        return ((RefContextImplementor) beanRef.context()).eventListeners();
    }

    private void scan(Object bean, Ref beanRef, EventListeners eventListeners) {
        Class<?> beanType = bean.getClass();
        while (beanType != null) {
            scan(bean, beanRef, beanType, eventListeners);
            beanType = beanType.getSuperclass();
        }
    }

    private void scan(Object bean, Ref beanRef, Class<?> beanType, EventListeners eventListeners) {
        for (Method method : beanType.getDeclaredMethods()) {

            ChangeListener changeListenerAnnotation = method.getAnnotation(ChangeListener.class);
            if (changeListenerAnnotation != null) {
                RefMatcher[] matchers = createMatchersFromPatterns(changeListenerAnnotation.pattern());
                eventListeners.add(new AnnotationChangeEventListener(beanRef, bean, matchers, method));
            }

            ActionListener actionListenerAnnotation = method.getAnnotation(ActionListener.class);
            if (actionListenerAnnotation != null) {
                String actionName = actionListenerAnnotation.name();
                if (actionName == null || actionName.isEmpty()) {
                    actionName = method.getName();
                }
                Class<?>[] parameterTypes = method.getParameterTypes();
                MethodParamInfo[] params = new MethodParamInfo[parameterTypes.length];
                for (int i = 0; i < method.getParameterAnnotations().length; i++) {
                    Annotation[] paramAnnotations = method.getParameterAnnotations()[i];
                    String paramName = getParamNameFromAnnotations(paramAnnotations);
                    if (paramName != null) {
                        params[i] = MethodParamInfo.namedActionParam(paramName, parameterTypes[i]);
                    } else if (Ref.class.isAssignableFrom(parameterTypes[i])) {
                        params[i] = MethodParamInfo.backRefParam(parameterTypes[i]);
                    } else {
                        throw new IllegalArgumentException("Method " + method + " has wrong parameters. Parameter " + i + " must be either annotated or be of java type " + Ref.class.getName());
                    }
                }
                RefMatcher[] matchers = createMatchersFromPatterns(actionListenerAnnotation.pattern());
                eventListeners.add(new AnnotationActionEventListener(beanRef,
                                                                     actionName,
                                                                     bean,
                                                                     matchers,
                                                                     method,
                                                                     params));
            }
        }
    }

    private String getParamNameFromAnnotations(Annotation[] paramAnnotations) {
        for (Annotation paramAnnotation : paramAnnotations) {
            if (paramAnnotation instanceof Param) {
                return ((Param) paramAnnotation).value();
            }
        }
        return null;
    }


    private RefMatcher[] createMatchersFromPatterns(String[] patterns) {
        if (patterns == null || patterns.length == 0 || patterns[0].isEmpty()) {
            return null;
        }
        RefMatcher[] matchers = new RefMatcher[patterns.length];
        for (int i = 0; i < patterns.length; i++) {
            matchers[i] = refMatcherFactory.getRefMatcher(patterns[i]);
        }
        return matchers;
    }

}
