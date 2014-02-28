package at.irian.ankor.connection;

import at.irian.ankor.session.ModelSession;

import java.util.Collection;

/**
 * @author Manfred Geiler
 */
@SuppressWarnings("UnusedDeclaration")
public interface ModelConnectionManager {

    ModelConnection getOrCreate(ModelSession modelSession, RemoteSystem remoteSystem);

    Collection<ModelConnection> getAllFor(ModelSession modelSession);

    Collection<ModelConnection> getAllFor(RemoteSystem remoteSystem);

    void invalidate(ModelConnection modelConnection);

}
