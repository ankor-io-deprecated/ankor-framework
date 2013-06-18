package at.irian.ankor.core.server;

import at.irian.ankor.core.action.ModelAction;
import at.irian.ankor.core.application.Application;
import at.irian.ankor.core.ref.ModelRef;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class SimpleAnkorServer extends AnkorServerBase {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleAnkorServer.class);

    private final String serverName;
    private SimpleAnkorServer remoteServer;

    public SimpleAnkorServer(Application application, String serverName) {
        super(application);
        this.serverName = serverName;
    }

    public void setRemoteServer(SimpleAnkorServer remoteServer) {
        this.remoteServer = remoteServer;
    }

    @Override
    public String toString() {
        return serverName;
    }

    @Override
    public void handleRemoteAction(String actionContextPath, ModelAction action) {
        super.handleRemoteAction(actionContextPath, action);
    }

    @Override
    public void handleRemoteChange(String propertyPath, Object newValue) {
        super.handleRemoteChange(propertyPath, newValue);
    }

    @Override
    protected void handleLocalChange(ModelRef modelRef, Object oldValue, Object newValue) {
        if (remoteServer != null) {
            LOG.info("Passing change to {} - {}: {} => {}", remoteServer, modelRef, oldValue, newValue);
            remoteServer.handleRemoteChange(modelRef.path(), newValue);
        }
    }

    @Override
    public void handleLocalAction(ModelRef modelRef, ModelAction action) {
        if (remoteServer != null) {
            LOG.info("sending action to {}:  ref={}, action={}", remoteServer, modelRef, action);
            remoteServer.handleRemoteAction(modelRef.path(), action);
        }
    }
}
