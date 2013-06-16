package at.irian.ankor.impl.el;

import javax.el.*;

/**
 * @author Manfred Geiler
 */
public class AnkorELResolver extends CompositeELResolver {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorELResolver.class);

    public AnkorELResolver() {
        add(new BeanELResolver());
        add(new ArrayELResolver());
        add(new MapELResolver());
        add(new ListELResolver());
        add(new ResourceBundleELResolver());
    }

}
