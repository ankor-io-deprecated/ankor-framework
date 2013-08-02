package at.irian.ankor.context;

import at.irian.ankor.event.EventListeners;
import at.irian.ankor.event.dispatch.EventDispatcherFactory;

/**
 * @author Manfred Geiler
 */
public class DefaultModelContextFactory implements ModelContextFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelContextFactory.class);

    private final EventDispatcherFactory eventDispatcherFactory;
    private final EventListeners globalEventListeners;

    public DefaultModelContextFactory(EventDispatcherFactory eventDispatcherFactory,
                                      EventListeners globalEventListeners) {
        this.eventDispatcherFactory = eventDispatcherFactory;
        this.globalEventListeners = globalEventListeners;
    }

    @Override
    public ModelContext createModelContext(String modelContextId) {
        return DefaultModelContext.create(eventDispatcherFactory, modelContextId, null, globalEventListeners);
    }

}
