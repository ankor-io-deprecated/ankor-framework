package at.irian.ankor.change;

import at.irian.ankor.messaging.AnkorIgnore;
import at.irian.ankor.session.Session;

/**
* @author Manfred Geiler
*/
public class RemoteChange extends Change {

    @AnkorIgnore
    private final Session session;

    private RemoteChange(ChangeType type, Object key, Object value, Session session) {
        super(type, key, value);
        this.session = session;
    }

    public static RemoteChange from(Change change, Session session) {
        return new RemoteChange(change.getType(), change.getKey(), change.getValue(), session);
    }

    public Session getSession() {
        return session;
    }
}
