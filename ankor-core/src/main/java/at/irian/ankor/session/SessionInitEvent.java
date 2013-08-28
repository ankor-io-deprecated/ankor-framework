package at.irian.ankor.session;

import at.irian.ankor.event.ModelEvent;
import at.irian.ankor.event.ModelEventListener;

/**
 * @author Manfred Geiler
 */
public class SessionInitEvent extends ModelEvent {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SessionInitEvent.class);

    public SessionInitEvent(Session session) {
        super(session);
    }

    public Session getSession() {
        return (Session)getSource();
    }

    @Override
    public boolean isAppropriateListener(ModelEventListener listener) {
        return listener instanceof Listener;
    }

    @Override
    public void processBy(ModelEventListener listener) {
        ((Listener)listener).processSessionInit(this);
    }

    public interface Listener {
        void processSessionInit(SessionInitEvent sessionInitEvent);
    }
}
