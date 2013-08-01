package at.irian.ankor.context;

import at.irian.ankor.event.dispatch.EventDispatcherFactory;

/**
 * @author Manfred Geiler
 */
public class DefaultModelContextFactory implements ModelContextFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelContextFactory.class);

    private final EventDispatcherFactory eventDispatcherFactory;

    public DefaultModelContextFactory(EventDispatcherFactory eventDispatcherFactory) {
        this.eventDispatcherFactory = eventDispatcherFactory;
    }

    @Override
    public ModelContext createModelContext(String modelContextId) {
        return DefaultModelContext.create(eventDispatcherFactory, modelContextId, null);
    }

}
