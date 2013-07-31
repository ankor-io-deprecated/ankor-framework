package at.irian.ankor.session;

import at.irian.ankor.context.ModelContext;

import java.util.Map;
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
    public Session getOrCreate(ModelContext modelContext, String remoteSystemId) {
        String sessionId = getSessionIdFrom(modelContext, remoteSystemId);
        Session session;
        session = sessionMap.get(sessionId);
        if (session == null) {
            synchronized (sessionMap) {
                session = sessionMap.get(sessionId);
                if (session == null) {
                    session = createAndInitSession(modelContext, sessionId);
                    sessionMap.put(sessionId, session);
                }
            }
        }
        return session;
    }

    private String getSessionIdFrom(ModelContext modelContext, String remoteSystemId) {
        return remoteSystemId + "_" + modelContext.getId();
    }

    protected Session createAndInitSession(ModelContext modelContext, String sessionId) {
        Session session = sessionFactory.create(modelContext, sessionId);
        session.init();
        return session;
    }

    @Override
    public void invalidate(Session session) {
        sessionMap.remove(session.getId());
        session.close();
    }

}
