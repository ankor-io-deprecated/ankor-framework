package at.irian.ankor.system;

import at.irian.ankor.event.source.Source;
import at.irian.ankor.session.Session;

/**
 * Event source for an event that is derived from an incoming message from a remote system.
 * A RemoteSource is always associated to a corresponding user session.
 *
 * @author Manfred Geiler
 */
public class RemoteSource implements Source {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RemoteSource.class);

    private final Session session;

    public RemoteSource(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }
}
