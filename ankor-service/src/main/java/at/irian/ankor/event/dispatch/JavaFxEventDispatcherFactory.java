package at.irian.ankor.event.dispatch;

import at.irian.ankor.session.ModelSession;

/**
 * @author Manfred Geiler
 */
public class JavaFxEventDispatcherFactory implements EventDispatcherFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(JavaFxEventDispatcherFactory.class);

    @Override
    public EventDispatcher createFor(ModelSession modelSession) {
        EventDispatcher simpleEventDispatcher = new SimpleEventDispatcher(modelSession.getEventListeners());
        EventDispatcher synchronisedSimpleEventDispatcher = new SynchronizedEventDispatcher(modelSession, simpleEventDispatcher);
        return new JavaFxEventDispatcher(synchronisedSimpleEventDispatcher);
    }

}
