package at.irian.ankor.session;

import at.irian.ankor.context.ModelContext;
import at.irian.ankor.ref.RefContext;

/**
 * @author Manfred Geiler
 */
public class SingletonSessionManager implements SessionManager {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SingletonSessionManager.class);

    private final SingletonSession session;

    public SingletonSessionManager(ModelContext modelContext, RefContext refContext) {
        this.session = new SingletonSession(modelContext, refContext);
    }

    @Override
    public Session getOrCreateSession(String id) {
        if (id != null) {
            if (session.getId() != null) {
                if (!session.getId().equals(id)) {
                    throw new IllegalStateException("wrong singleton session id");
                }
            } else {
                session.setId(id);
            }
        }
        return session;
    }

    @Override
    public void invalidateSession(String id) {
        throw new UnsupportedOperationException();
    }
}
