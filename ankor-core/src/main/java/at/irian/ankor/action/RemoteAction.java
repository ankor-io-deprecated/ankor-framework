package at.irian.ankor.action;

import at.irian.ankor.messaging.AnkorIgnore;
import at.irian.ankor.session.Session;

import java.util.Map;

/**
 * An {@link Action} that happened on a remote system.
 *
* @author Manfred Geiler
*/
public class RemoteAction extends Action {

    @AnkorIgnore
    private final Session session;

    public RemoteAction(Session session, String actionName, Map<String, Object> params) {
        super(actionName, params);
        this.session = session;
    }

    public Session getSession() {
        return session;
    }
}
