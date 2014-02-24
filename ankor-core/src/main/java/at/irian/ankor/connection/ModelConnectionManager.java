package at.irian.ankor.connection;

import at.irian.ankor.context.ModelContext;

import java.util.Collection;

/**
 * @author Manfred Geiler
 */
@SuppressWarnings("UnusedDeclaration")
public interface ModelConnectionManager {

    ModelConnection getOrCreate(ModelContext modelContext, RemoteSystem remoteSystem);

    Collection<ModelConnection> getAllFor(ModelContext modelContext);

    Collection<ModelConnection> getAllFor(RemoteSystem remoteSystem);

    void invalidate(ModelConnection modelConnection);

}
