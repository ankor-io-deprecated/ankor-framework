package at.irian.ankor.dispatch;

import at.irian.ankor.action.Action;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.session.Session;

/**
 * @author Manfred Geiler
 */
public class SessionSynchronisedDispatcher extends DispatcherBase {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SessionSynchronisedDispatcher.class);

    private final Session session;

    public SessionSynchronisedDispatcher(Session session) {
        this.session = session;
    }

    @Override
    public void dispatchAction(Ref actionProperty, Action action) {
        synchronized (session) {
            super.dispatchAction(actionProperty, action);
        }
    }

    @Override
    public void dispatchChange(Ref changedProperty, Object newValue) {
        synchronized (session) {
            super.dispatchChange(changedProperty, newValue);
        }
    }
}
