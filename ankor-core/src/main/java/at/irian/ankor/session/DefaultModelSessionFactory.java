package at.irian.ankor.session;

import at.irian.ankor.application.ApplicationInstance;
import at.irian.ankor.event.EventListeners;
import at.irian.ankor.event.dispatch.EventDispatcherFactory;
import at.irian.ankor.ref.RefContextFactory;

/**
 * @author Manfred Geiler
 */
public class DefaultModelSessionFactory implements ModelSessionFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelSessionFactory.class);

    private final EventDispatcherFactory eventDispatcherFactory;
    private final EventListeners defaultEventListeners;
    private final RefContextFactory refContextFactory;

    public DefaultModelSessionFactory(EventDispatcherFactory eventDispatcherFactory,
                                      EventListeners defaultEventListeners,
                                      RefContextFactory refContextFactory) {
        this.eventDispatcherFactory = eventDispatcherFactory;
        this.defaultEventListeners = defaultEventListeners;
        this.refContextFactory = refContextFactory;
    }

    @Override
    public ModelSession createModelSession(ApplicationInstance applicationInstance) {
        return DefaultModelSession.create(eventDispatcherFactory,
                                          defaultEventListeners,
                                          applicationInstance,
                                          refContextFactory);
    }

}
