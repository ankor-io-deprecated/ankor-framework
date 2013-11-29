package at.irian.ankor.event.source;

import at.irian.ankor.session.Session;

/**
 * Remote source of an event.
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
