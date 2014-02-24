package at.irian.ankor.connection;

import at.irian.ankor.session.ModelSession;

/**
 * @author Manfred Geiler
 */
public interface ModelConnectionFactory {

    ModelConnection create(ModelSession modelSession, RemoteSystem remoteSystem);

    void close();
}
