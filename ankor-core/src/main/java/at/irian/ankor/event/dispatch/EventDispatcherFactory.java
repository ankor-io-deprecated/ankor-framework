package at.irian.ankor.event.dispatch;

import at.irian.ankor.session.ModelSession;

/**
 * @author Manfred Geiler
 */
public interface EventDispatcherFactory {

    EventDispatcher createFor(ModelSession modelSession);

}
