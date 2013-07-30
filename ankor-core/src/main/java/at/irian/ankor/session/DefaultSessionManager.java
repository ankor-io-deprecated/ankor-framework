package at.irian.ankor.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Manfred Geiler
 */
public class DefaultSessionManager implements SessionManager {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultSessionManager.class);

    private final Map<String, Session> sessionMap = new ConcurrentHashMap<String, Session>();
    private final SessionFactory sessionFactory;
    private final SessionIdGenerator sessionIdGenerator;

    public DefaultSessionManager(SessionFactory sessionFactory, SessionIdGenerator sessionIdGenerator) {
        this.sessionFactory = sessionFactory;
        this.sessionIdGenerator = sessionIdGenerator;
    }

    @Override
    public Session getOrCreateSession(String id) {
        Session session;
        if (id == null) {
            id = sessionIdGenerator.create();
            session = createAndInitSession(id);
            sessionMap.put(id, session);
        } else {
            session = sessionMap.get(id);
            if (session == null) {
                synchronized (sessionMap) {
                    session = sessionMap.get(id);
                    if (session == null) {
                        session = createAndInitSession(id);
                        sessionMap.put(id, session);
                    }
                }
            }
        }
        return session;
    }

    protected Session createAndInitSession(String id) {
        Session session = sessionFactory.create(id);
        session.init();
        return session;
    }

    @Override
    public void invalidateSession(String id) {
        Session session = sessionMap.get(id);
        if (session != null) {
            synchronized (sessionMap) {
                session = sessionMap.get(id);
                if (session != null) {
                    session.close();
                    sessionMap.remove(id);
                }
            }
        }
    }

}
