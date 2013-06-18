package at.irian.ankor.core.application;

import at.irian.ankor.core.el.BeanResolver;

import java.util.HashMap;
import java.util.Map;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class SimpleApplication extends Application {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleApplication.class);


    protected SimpleApplication(Class<?> modelType, BeanResolver beanResolver) {
        super(modelType, beanResolver);
    }

    public static SimpleApplication withModelType(Class<?> modelType) {
        return new SimpleApplication(modelType, new MyBeanResolver());
    }

    public SimpleApplication withBean(String beanName, Object bean) {
        ((MyBeanResolver)getBeanResolver()).beans.put(beanName, bean);
        return this;
    }

    private static class MyBeanResolver implements BeanResolver {

        private final Map<String, Object> beans = new HashMap<String, Object>();

        @Override
        public Object resolveByName(String beanName) {
            return beans.get(beanName);
        }
    }
}
