package at.irian.ankor.connection;

import at.irian.ankor.session.ModelSession;

import java.util.Collection;
import java.util.Collections;

/**
 * ModelConnectionManager that manages exactly one ModelConnection.
 * On a call to {@link ModelConnectionManager#getOrCreate(at.irian.ankor.session.ModelSession, RemoteSystem)}
 * the SingletonModelConnectionManager always returns the same SingletonModelConnection instance.
 * Calling {@link ModelConnectionManager#getOrCreate(at.irian.ankor.session.ModelSession, RemoteSystem)} with a
 * different ModelSession than this singleton's ModelSession causes an IllegalStateException.
 *
 * @author Manfred Geiler
 * @see SingletonModelConnection
 */
public class SingletonModelConnectionManager implements ModelConnectionManager {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SingletonModelConnectionManager.class);

    private ModelConnection modelConnection;

    public void setModelConnection(ModelConnection modelConnection) {
        this.modelConnection = modelConnection;
    }

    @Override
    public ModelConnection getOrCreate(ModelSession modelSession, RemoteSystem remoteSystem) {
        if (!modelSession.equals(modelConnection.getModelSession())) {
            throw new IllegalStateException("wrong model session " + modelSession
                                            + " - expected " + modelConnection.getModelSession());
        }
        return modelConnection;
    }

    @Override
    public Collection<ModelConnection> getAllFor(ModelSession modelSession) {
        if (!modelSession.equals(modelConnection.getModelSession())) {
            throw new IllegalStateException("wrong model session " + modelSession
                                            + " - expected " + modelConnection.getModelSession());
        }
        return Collections.singleton(modelConnection);
    }

    @Override
    public Collection<ModelConnection> getAllFor(RemoteSystem remoteSystem) {
        return Collections.singleton(modelConnection);
    }

    @Override
    public void invalidate(ModelConnection modelConnection) {
        throw new UnsupportedOperationException();
    }

    public ModelConnection getModelConnection() {
        return modelConnection;
    }
}
