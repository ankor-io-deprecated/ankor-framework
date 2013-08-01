package at.irian.ankor.session;

import at.irian.ankor.context.ModelContext;

import java.util.Collection;

/**
 * @author Manfred Geiler
 */
public interface SessionManager {

    Session getOrCreate(ModelContext modelContext, RemoteSystem remoteSystem);

    Collection<Session> getAllFor(ModelContext modelContext);

    void invalidate(Session session);

}
