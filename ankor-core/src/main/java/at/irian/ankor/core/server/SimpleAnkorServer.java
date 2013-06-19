package at.irian.ankor.core.server;

import at.irian.ankor.core.action.ModelAction;
import at.irian.ankor.core.application.DefaultApplication;
import at.irian.ankor.core.ref.Ref;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class SimpleAnkorServer extends ELAnkorServer {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleAnkorServer.class);

    private final String serverName;
    private SimpleAnkorServer remoteServer;

    public SimpleAnkorServer(DefaultApplication application, String serverName) {
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

    public void receiveAction(String actionContextPath, ModelAction action) {
        receiveAction(application.getRefFactory().ref(actionContextPath), action);
    }

    public void receiveChange(String changedPath, Object newValue) {
        receiveChange(application.getRefFactory().ref(changedPath), newValue);
    }

    @Override
    protected void sendChange(Ref changedRef, Object newValue) {
        if (remoteServer != null) {
            LOG.info("passing change to {} - {} => {}", remoteServer, changedRef, newValue);
            remoteServer.receiveChange(changedRef.path(), newValue);
        }
    }

    @Override
    public void sendAction(Ref actionContextRef, ModelAction action) {
        if (remoteServer != null) {
            LOG.info("passing action to {}:  ref={}, action={}", remoteServer, actionContextRef, action);
            remoteServer.receiveAction(actionContextRef.path(), action);
        }
    }
}
