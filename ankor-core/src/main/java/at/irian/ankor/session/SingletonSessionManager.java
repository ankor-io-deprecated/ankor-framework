package at.irian.ankor.session;

import at.irian.ankor.context.ModelContext;
import at.irian.ankor.messaging.MessageSender;
import at.irian.ankor.ref.RefContext;

import java.util.Collection;
import java.util.Collections;

/**
 * SessionManager that manages exactly one Session.
 * On a call to {@link SessionManager#getOrCreate(at.irian.ankor.context.ModelContext, RemoteSystem)} the SingletonSessionManager always returns the same
 * SingletonSession instance.
 * Calling {@link SessionManager#getOrCreate(at.irian.ankor.context.ModelContext, RemoteSystem)} with a different ModelContext than this instance's
 * singleton Session's ModelContext causes an IllegalStateException.
 *
 * @author Manfred Geiler
 * @see SingletonSession
 */
public class SingletonSessionManager implements SessionManager {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SingletonSessionManager.class);

    private final Session session;

    public SingletonSessionManager(ModelContext modelContext, RefContext refContext, MessageSender messageSender) {
        this.session = new SingletonSession(modelContext, refContext, messageSender);
    }

    @Override
    public Session getOrCreate(ModelContext modelContext, RemoteSystem remoteSystem) {
        if (!modelContext.equals(session.getModelContext())) {
            throw new IllegalStateException("wrong model context " + modelContext + " - expected " + session.getModelContext());
        }
        return session;
    }

    @Override
    public Collection<Session> getAllFor(ModelContext modelContext) {
        if (!modelContext.equals(session.getModelContext())) {
            throw new IllegalStateException("wrong model context " + modelContext + " - expected " + session.getModelContext());
        }
        return Collections.singleton(session);
    }

    @Override
    public void invalidate(Session session) {
        throw new UnsupportedOperationException();
    }

    public Session getSession() {
        return session;
    }
}
