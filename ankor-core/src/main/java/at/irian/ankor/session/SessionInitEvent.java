package at.irian.ankor.session;

import at.irian.ankor.event.ModelEvent;
import at.irian.ankor.event.ModelEventListener;

/**
 * @author Manfred Geiler
 */
public class SessionInitEvent extends ModelEvent {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SessionInitEvent.class);

    private final Session session;

    public SessionInitEvent(Session session) {
        super(session.getRefContext().refFactory().rootRef());
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    @Override
    public boolean isAppropriateListener(ModelEventListener listener) {
        return listener instanceof Listener;
    }

    @Override
    public void processBy(ModelEventListener listener) {
        ((Listener)listener).processModelInit(this);
    }

    public interface Listener {
        void processModelInit(SessionInitEvent sessionInitEvent);
    }
}
