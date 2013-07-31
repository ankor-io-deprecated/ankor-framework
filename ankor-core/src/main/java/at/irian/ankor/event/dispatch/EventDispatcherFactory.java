package at.irian.ankor.event.dispatch;

import at.irian.ankor.session.Session;

/**
 * @author Manfred Geiler
 */
public interface EventDispatcherFactory {
    EventDispatcher createFor(Session session);
}
