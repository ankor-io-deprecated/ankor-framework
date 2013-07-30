package at.irian.ankor.dispatch;

import at.irian.ankor.event.ModelEvent;
import at.irian.ankor.session.Session;

/**
 * @author Manfred Geiler
 */
public class SessionSynchronisedEventDispatcher extends SimpleEventDispatcher {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SessionSynchronisedEventDispatcher.class);

    private final Session session;

    public SessionSynchronisedEventDispatcher(Session session) {
        super(session.getModelContext().getEventListeners());
        this.session = session;
    }

    @Override
    public void dispatch(ModelEvent event) {
        synchronized (session) {
            super.dispatch(event);
        }
    }

    @Override
    public void close() {
    }
}
