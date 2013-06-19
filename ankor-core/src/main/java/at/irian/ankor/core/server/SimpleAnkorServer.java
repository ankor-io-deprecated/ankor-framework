package at.irian.ankor.core.server;

import at.irian.ankor.core.action.ModelAction;
import at.irian.ankor.core.application.Application;
import at.irian.ankor.core.ref.Ref;

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
    protected void handleLocalChange(Ref ref, Object newValue) {
        if (remoteServer != null) {
            LOG.info("Passing change to {} - {} => {}", remoteServer, ref, newValue);
            remoteServer.handleRemoteChange(ref.path(), newValue);
        }
    }

    @Override
    public void handleLocalAction(Ref ref, ModelAction action) {
        if (remoteServer != null) {
            LOG.info("sending action to {}:  ref={}, action={}", remoteServer, ref, action);
            remoteServer.handleRemoteAction(ref.path(), action);
        }
    }
}
