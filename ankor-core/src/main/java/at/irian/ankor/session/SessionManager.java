package at.irian.ankor.session;

import at.irian.ankor.context.ModelContext;

/**
 * @author Manfred Geiler
 */
public interface SessionManager {

    Session getOrCreate(ModelContext modelContext, String remoteSystemId);

    void invalidate(Session session);

}
