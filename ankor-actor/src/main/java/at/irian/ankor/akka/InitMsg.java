package at.irian.ankor.akka;

import at.irian.ankor.session.Session;

/**
 * @author Manfred Geiler
 */
public class InitMsg {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(InitMsg.class);

    private final Session session;

    public InitMsg(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }
}
