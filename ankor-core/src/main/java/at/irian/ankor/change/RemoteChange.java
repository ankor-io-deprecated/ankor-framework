package at.irian.ankor.change;

import at.irian.ankor.messaging.AnkorIgnore;
import at.irian.ankor.session.Session;

/**
* @author Manfred Geiler
*/
public class RemoteChange extends Change {

    @AnkorIgnore
    private final Session session;

    public RemoteChange(Session session, Object newValue) {
        super(newValue);
        this.session = session;
    }

    public Session getSession() {
        return session;
    }
}
