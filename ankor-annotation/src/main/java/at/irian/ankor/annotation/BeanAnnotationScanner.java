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

    public void scan(Object bean, Ref beanRef, Lifeline lifeline) {
        scan(bean, beanRef, lifeline, getEventListeners(beanRef));
    }

    private EventListeners getEventListeners(Ref beanRef) {
        return ((RefContextImplementor) beanRef.context()).eventListeners();
    }

    private void scan(Object bean, Ref beanRef, Lifeline lifeline, EventListeners eventListeners) {
        Class<?> beanType = bean.getClass();
        while (beanType != null) {
            scan(bean, beanRef, lifeline, beanType, eventListeners);
            beanType = beanType.getSuperclass();
        }
    }

    private void scan(Object bean, Ref beanRef, Lifeline lifeline, Class<?> beanType, EventListeners eventListeners) {
        for (Method method : beanType.getDeclaredMethods()) {

            ChangeListener changeListenerAnnotation = method.getAnnotation(ChangeListener.class);
            if (changeListenerAnnotation != null) {
                RefMatcher[] matchers = createMatchersFromPatterns(changeListenerAnnotation.pattern());
                eventListeners.add(new AnnotationChangeEventListener(beanRef, bean, matchers, method, lifeline));
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
                RefMatcher[] matchers = createMatchersFromPatterns(actionListenerAnnotation.pattern());
                eventListeners.add(new AnnotationActionEventListener(beanRef,
                                                                     actionName,
                                                                     bean,
                                                                     matchers,
                                                                     method,
                                                                     paramNames,
                                                                     lifeline));
            }
        }
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
