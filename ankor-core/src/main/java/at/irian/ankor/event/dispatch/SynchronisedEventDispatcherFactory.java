package at.irian.ankor.event.dispatch;

import at.irian.ankor.session.ModelSession;

/**
 * @author Manfred Geiler
 */
public class SynchronisedEventDispatcherFactory implements EventDispatcherFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SynchronisedEventDispatcherFactory.class);

    @Override
    public EventDispatcher createFor(ModelSession modelSession) {
        return new SynchronisedEventDispatcher(modelSession);
    }

    @Override
    public void close() {
    }
}
