package at.irian.ankor.session;

import at.irian.ankor.event.EventListeners;
import at.irian.ankor.event.dispatch.EventDispatcherFactory;

/**
 * @author Manfred Geiler
 */
public class DefaultModelSessionFactory implements ModelSessionFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelSessionFactory.class);

    private final EventDispatcherFactory eventDispatcherFactory;
    private final EventListeners globalEventListeners;

    public DefaultModelSessionFactory(EventDispatcherFactory eventDispatcherFactory,
                                      EventListeners globalEventListeners) {
        this.eventDispatcherFactory = eventDispatcherFactory;
        this.globalEventListeners = globalEventListeners;
    }

    @Override
    public ModelSession createModelSession(String modelSessionId) {
        return DefaultModelSession.create(eventDispatcherFactory,
                                          modelSessionId,
                                          globalEventListeners);
    }

}
