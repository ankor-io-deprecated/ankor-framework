package at.irian.ankor.connection;

import at.irian.ankor.context.ModelContext;

import java.util.Collection;
import java.util.Collections;

/**
 * ModelConnectionManager that manages exactly one ModelConnection.
 * On a call to {@link ModelConnectionManager#getOrCreate(at.irian.ankor.context.ModelContext, RemoteSystem)}
 * the SingletonModelConnectionManager always returns the same SingletonModelConnection instance.
 * Calling {@link ModelConnectionManager#getOrCreate(at.irian.ankor.context.ModelContext, RemoteSystem)} with a
 * different ModelContext than this singleton's ModelContext causes an IllegalStateException.
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
    public ModelConnection getOrCreate(ModelContext modelContext, RemoteSystem remoteSystem) {
        if (!modelContext.equals(modelConnection.getModelContext())) {
            throw new IllegalStateException("wrong model context " + modelContext + " - expected " + modelConnection.getModelContext());
        }
        return modelConnection;
    }

    @Override
    public Collection<ModelConnection> getAllFor(ModelContext modelContext) {
        if (!modelContext.equals(modelConnection.getModelContext())) {
            throw new IllegalStateException("wrong model context " + modelContext + " - expected " + modelConnection.getModelContext());
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
