package at.irian.ankor.core.server;

import at.irian.ankor.core.application.Application;
import at.irian.ankor.core.listener.ModelActionListener;
import at.irian.ankor.core.listener.ModelChangeListener;
import at.irian.ankor.core.ref.ModelRef;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class AnkorServer implements ModelChangeListener, ModelActionListener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorServer.class);

    private final String serverName;
    private final Application application;
    private final RemoteActionHandler remoteActionHandler;
    private final RemoteChangeHandler remoteChangeHandler;
    private AnkorServer remoteServer;

    public AnkorServer(String serverName, Application application) {
        this.serverName = serverName;
        this.application = application;
        this.remoteActionHandler = new RemoteActionHandler(application.getListenerRegistry());
        this.remoteChangeHandler = new RemoteChangeHandler(application.getListenerRegistry());
    }

    public void init() {
        application.getListenerRegistry().registerLocalChangeListener(null, this);
        application.getListenerRegistry().registerLocalActionListener(null, this);
    }

    public void close() {
        application.getListenerRegistry().unregisterListener(this);
    }

    @Override
    public void handleModelAction(ModelRef modelRef, String action) {
        handleLocalAction(modelRef, action);
    }

    @Override
    public void beforeModelChange(ModelRef modelRef, Object oldValue, Object newValue) {
    }

    @Override
    public void afterModelChange(ModelRef modelRef, Object oldValue, Object newValue) {
        handleLocalChange(modelRef, oldValue, newValue);
    }



    public void handleRemoteAction(String path, String action) {
        ModelRef modelRef = application.getRefFactory().ref(path);
        handleRemoteAction(modelRef, action);
    }

    public void handleRemoteAction(ModelRef modelRef, String action) {
        remoteActionHandler.handleRemoteAction(modelRef, action);
    }

    public void handleRemoteChange(String path, Object newValue) {
        ModelRef modelRef = application.getRefFactory().ref(path);
        handleRemoteChange(modelRef, newValue);
    }

    public void handleRemoteChange(ModelRef modelRef, Object newValue) {
        remoteChangeHandler.handleRemoteChange(modelRef, newValue);
    }



    public void setRemoteServer(AnkorServer remoteServer) {
        this.remoteServer = remoteServer;
    }

    protected void handleLocalChange(ModelRef modelRef, Object oldValue, Object newValue) {
        if (remoteServer != null) {
            LOG.info("sending change to {}: ref={}, old={}, new={}", remoteServer.serverName, modelRef, oldValue, newValue);
            remoteServer.handleRemoteChange(modelRef.path(), newValue);
        }
    }

    public void handleLocalAction(ModelRef modelRef, String action) {
        if (remoteServer != null) {
            LOG.info("sending action to {}:  ref={}, action={}", remoteServer.serverName, modelRef, action);
            remoteServer.handleRemoteAction(modelRef.path(), action);
        }
    }
}
