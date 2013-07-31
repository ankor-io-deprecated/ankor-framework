package at.irian.ankor.session;

import at.irian.ankor.context.ModelContext;
import at.irian.ankor.ref.RefContext;

/**
 * SessionManager that manages exactly one Session.
 * On a call to {@link #getOrCreate(at.irian.ankor.context.ModelContext, String)} the SingletonSessionManager always returns the same
 * SingletonSession instance.
 * Calling {@link #getOrCreate(at.irian.ankor.context.ModelContext, String)} with a different ModelContext than this instance's
 * singleton Session's ModelContext causes an IllegalStateException.
 *
 * @author Manfred Geiler
 * @see SingletonSession
 */
public class SingletonSessionManager implements SessionManager {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SingletonSessionManager.class);

    private final Session session;

    public SingletonSessionManager(ModelContext modelContext, RefContext refContext) {
        this.session = new SingletonSession("singletonSession", modelContext, refContext);
    }

    @Override
    public Session getOrCreate(ModelContext modelContext, String remoteSystemId) {
        if (!modelContext.equals(session.getModelContext())) {
            throw new IllegalStateException("wrong model context " + modelContext + " - expected " + session.getModelContext());
        }
        return session;
    }

    @Override
    public void invalidate(Session session) {
        throw new UnsupportedOperationException();
    }

    public Session getSession() {
        return session;
    }
}
