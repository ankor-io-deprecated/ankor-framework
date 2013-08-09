package at.irian.ankor.annotation;

import at.irian.ankor.change.ChangeEvent;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.path.PathSyntax;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.match.RefMatcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Spiegl
 */
public class BeanAnnotationChangeEventListener extends ChangeEventListener {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BeanAnnotationChangeEventListener.class);

    private final Object bean;
    private final PathSyntax pathSyntax;
    private final List<ChangeTarget> changeTargets;

    public BeanAnnotationChangeEventListener(Object bean, String pathPrefix, PathSyntax pathSyntax) {
        super(null); // always global
        this.bean = bean;
        this.pathSyntax = pathSyntax;
        this.changeTargets = scan(pathPrefix);
    }

    private List<ChangeTarget> scan(String pathPrefix) {
        List<ChangeTarget> result = new ArrayList<ChangeTarget>();
        Class<?> beanType = bean.getClass();
        for (Method method : beanType.getMethods()) {
            for (Annotation methodAnnotation : method.getDeclaredAnnotations()) {
                if (methodAnnotation instanceof ChangeListener) {
                    ChangeListener changeListenerAnnotation = (ChangeListener) methodAnnotation;
                    result.add(new ChangeTarget(method, pathPrefix, changeListenerAnnotation.pattern()));
                }
            }
        }
        return result;
    }

    @Override
    public void process(ChangeEvent event) {
        for (ChangeTarget target : changeTargets) {
            for (String pattern : target.getPatterns()) {

                RefMatcher matcher = new RefMatcher(pathSyntax, pattern);
                RefMatcher.Result result = matcher.match(event.getChangedProperty());
                if (result.isMatch()) {
                    Method method = target.getMethod();
                    try {
                        if (target.isPassChangeProperty()) {
                            method.invoke(bean, event.getChangedProperty());
                        } else {
                            method.invoke(bean);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(String.format("Error invoking change listener method %s on bean %s",
                                                                 method,
                                                                 bean), e);
                    }
                }

            }
        }
    }

    static class ChangeTarget {
        private final Method method;
        private boolean passChangeProperty;
        private final String[] patterns;

        public ChangeTarget(Method method, String pathPrefix, String[] patterns) {
            this.method = method;
            if (method.getParameterTypes().length == 1 && method.getParameterTypes()[0].isAssignableFrom(Ref.class)) {
                passChangeProperty = true;
            }
            if (pathPrefix != null) {
                for (int i = 0; i < patterns.length; i++) {
                    patterns[i] = pathPrefix + "." + patterns[i];
                }
            }
            this.patterns = patterns;
        }

        Method getMethod() {
            return method;
        }

        String[] getPatterns() {
            return patterns;
        }

        boolean isPassChangeProperty() {
            return passChangeProperty;
        }
    }
}
