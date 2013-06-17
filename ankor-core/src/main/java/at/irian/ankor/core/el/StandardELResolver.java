package at.irian.ankor.core.el;

import javax.el.*;

/**
 * @author Manfred Geiler
 */
class StandardELResolver extends CompositeELResolver {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(StandardELResolver.class);

    public StandardELResolver() {
        add(new MapELResolver());
        add(new ArrayELResolver());
        add(new ListELResolver());
        add(new ResourceBundleELResolver());
        add(new BeanELResolver());
    }

}
