package at.irian.ankor.core.application;

import at.irian.ankor.core.el.BeanResolver;
import at.irian.ankor.core.el.BeanResolverELContext;
import at.irian.ankor.core.el.StandardELContext;

import javax.el.ExpressionFactory;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class DefaultApplication extends ELApplication {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Application.class);

    private final BeanResolver beanResolver;

    public DefaultApplication(Class<?> modelType, BeanResolver beanResolver) {
        super(modelType,
              new DefaultConfig(),
              ExpressionFactory.newInstance(),
              new BeanResolverELContext(new StandardELContext(), beanResolver));
        this.beanResolver = beanResolver;
    }

    protected BeanResolver getBeanResolver() {
        return beanResolver;
    }


}
