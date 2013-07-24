package at.irian.ankor.annotation;

import at.irian.ankor.change.ChangeEvent;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.path.PathSyntax;
import at.irian.ankor.ref.RefMatcher;
import at.irian.ankor.system.BeanResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Spiegl
 */
public class BeanAnnotationChangeEventListener extends ChangeEventListener {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BeanAnnotationChangeEventListener.class);

    private final BeanResolver beanResolver;
    private final PathSyntax pathSyntax;
    private final List<ChangeTarget> changeTargets;

    public BeanAnnotationChangeEventListener(BeanResolver beanResolver, PathSyntax pathSyntax) {
        super(null); // always global
        this.beanResolver = beanResolver;
        this.pathSyntax = pathSyntax;
        this.changeTargets = scan(beanResolver);
    }

    private List<ChangeTarget> scan(BeanResolver beanResolver) {
        List<ChangeTarget> result = new ArrayList<ChangeTarget>();
        for (String beanName : beanResolver.getBeanDefinitionNames()) {
            Object bean = beanResolver.resolveByName(beanName);
            Class<?> beanType = bean.getClass();
            for (Method method : beanType.getMethods()) {
                for (Annotation methodAnnotation : method.getDeclaredAnnotations()) {
                    if (methodAnnotation instanceof ChangeListener) {
                        ChangeListener changeListenerAnnot = (ChangeListener) methodAnnotation;
                        result.add(new ChangeTarget(beanName, method, changeListenerAnnot.pattern()));
                    }
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
                    Object bean = beanResolver.resolveByName(target.getBeanName());
                    Method method = target.getMethod();
                    try {
                        method.invoke(bean, result.getWatchedRefs().toArray());
                    } catch (Exception e) {
                        throw new RuntimeException(String.format("Error invoking change listener method %s on bean %s",
                                                                 method,
                                                                 bean));
                    }
                }

            }
        }
    }

    static class ChangeTarget {
        private final String beanName;
        private final Method method;
        private final String[] patterns;

        public ChangeTarget(String beanName, Method method, String[] patterns) {
            this.beanName = beanName;
            this.method = method;
            this.patterns = patterns;
        }

        String getBeanName() {
            return beanName;
        }

        Method getMethod() {
            return method;
        }

        String[] getPatterns() {
            return patterns;
        }
    }
}
