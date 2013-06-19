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

    public void receiveAction(String modelContextPath, ModelAction action) {
        receiveAction(application.getRefFactory().ref(modelContextPath), action);
    }

    public void receiveChange(String changedPath, Object newValue) {
        receiveChange(null, changedPath, newValue);
    }

    public void receiveChange(String modelContextPath, String changedPath, Object newValue) {
        receiveChange(modelContextPath != null ? application.getRefFactory().ref(modelContextPath) : null,
                      application.getRefFactory().ref(changedPath),
                      newValue);
    }

    @Override
    protected void sendChange(Ref modelContext, Ref changedProperty, Object newValue) {
        if (remoteServer != null) {
            LOG.info("passing change to {} - {} => {} / context = {}", remoteServer, changedProperty, newValue, modelContext);
            remoteServer.receiveChange(modelContext != null ? modelContext.path() : null,
                                       changedProperty.path(),
                                       newValue);
        }
    }

    @Override
    public void sendAction(Ref modelContext, ModelAction action) {
        if (remoteServer != null) {
            LOG.info("passing action to {}: action = {} / context = {}", remoteServer, action, modelContext);
            remoteServer.receiveAction(modelContext.path(), action);
        }
    }
}
