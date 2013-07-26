package at.irian.ankor.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Manfred Geiler
 */
public class DefaultSessionManager implements SessionManager {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultSessionManager.class);

    private final Map<String, Session> sessionMap = new ConcurrentHashMap<String, Session>();
    private final SessionFactory sessionFactory;

    public DefaultSessionManager(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Session getOrCreateSession(String id) {
        Session session;
        if (id == null) {
            id = UUID.randomUUID().toString();
            session = sessionFactory.create(id);
            sessionMap.put(id, session);
        } else {
            session = sessionMap.get(id);
            if (session == null) {
                synchronized (sessionMap) {
                    session = sessionMap.get(id);
                    if (session == null) {
                        session = sessionFactory.create(id);
                        sessionMap.put(id, session);
                    }
                }
            }
        }
        return session;
    }

    @Override
    public void invalidateSession(String id) {
        Session session = sessionMap.get(id);
        if (session != null) {
            synchronized (sessionMap) {
                session = sessionMap.get(id);
                if (session != null) {
                    session.invalidate();
                    sessionMap.remove(id);
                }
            }
        }
    }

}
