package at.irian.ankor.connection;

import at.irian.ankor.context.ModelContext;

/**
 * @author Manfred Geiler
 */
public interface ModelConnectionFactory {

    ModelConnection create(ModelContext modelContext, RemoteSystem remoteSystem);

    void close();
}
