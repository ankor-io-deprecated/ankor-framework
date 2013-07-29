package at.irian.ankor.dispatch;

import at.irian.ankor.session.Session;

/**
 * @author Manfred Geiler
 */
public class SessionSynchronisedEventDispatcherFactory implements EventDispatcherFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SessionSynchronisedEventDispatcherFactory.class);

    @Override
    public EventDispatcher createFor(Session session) {
        return new SessionSynchronisedEventDispatcher(session);
    }
}
