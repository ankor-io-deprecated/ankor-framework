package at.irian.ankor.system;

import at.irian.ankor.event.source.Source;
import at.irian.ankor.connection.ModelConnection;

/**
 * Event source for an event that is derived from an incoming message from a remote system.
 * A RemoteSource is always associated to a corresponding ModelConnection.
 *
 * @author Manfred Geiler
 */
public class RemoteSource implements Source {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RemoteSource.class);

    private final ModelConnection modelConnection;

    public RemoteSource(ModelConnection modelConnection) {
        this.modelConnection = modelConnection;
    }

    public ModelConnection getModelConnection() {
        return modelConnection;
    }
}
