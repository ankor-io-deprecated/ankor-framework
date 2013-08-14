package at.irian.ankor.el;

import javax.el.*;

/**
 * @author Manfred Geiler
 */
class StandardELResolver extends CompositeELResolver {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(StandardELResolver.class);

    public StandardELResolver(ELResolver... additionalResolvers) {
        for (ELResolver additionalResolver : additionalResolvers) {
            add(additionalResolver);
        }
        add(new MapELResolver());
        add(new FriendlyArrayELResolver());
        add(new FriendlyListELResolver());
        add(new ResourceBundleELResolver());
        add(new BeanELResolver());
    }

}
