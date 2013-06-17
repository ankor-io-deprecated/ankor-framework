package at.irian.ankor.core.server;

import at.irian.ankor.core.listener.ModelActionListener;
import at.irian.ankor.core.listener.ModelChangeListener;
import at.irian.ankor.core.application.Application;
import at.irian.ankor.core.ref.ModelRef;

import java.util.*;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class AnkorServer {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorServer.class);

    private final String serverName;
    private final Application application;
    private AnkorServer remoteServer;

    public AnkorServer(String serverName, Application application) {
        this.serverName = serverName;
        this.application = application;
        this.application.getListenerRegistry().registerLocalChangeListener(null,
                                                                           new ClientNotifyingChangeListener(this));
        this.application.getListenerRegistry().registerLocalActionListener(null,
                                                                           new ClientNotifyingActionListener(this));
    }

    public void handleRemoteAction(String path, String action) {
        ModelRef modelRef = application.getRefFactory().ref(path);
        handleRemoteAction(modelRef, action);
    }

    private void handleRemoteAction(ModelRef modelRef, String action) {
        Collection<ModelActionListener> listeners = application.getListenerRegistry().getRemoteActionListenersFor(modelRef);
        for (ModelActionListener listener : listeners) {
            listener.handleModelAction(modelRef, action);
        }
    }

    public void handleRemoteChange(String path, Object newValue) {
        ModelRef modelRef = application.getRefFactory().ref(path);
        handleRemoteChange(modelRef, newValue);
    }

    private void handleRemoteChange(ModelRef modelRef, Object newValue) {
        Object oldValue = modelRef.getValue();
        Collection<ModelChangeListener> listeners
                = application.getListenerRegistry().getRemoteChangeListenersFor(modelRef);

        for (ModelChangeListener listener : listeners) {
            listener.beforeModelChange(modelRef, oldValue, newValue);
        }

        modelRef.unwatched().setValue(newValue);

        if (isNil(newValue)) {
            application.getListenerRegistry().unregisterAllListenersFor(modelRef);
        }

        for (ModelChangeListener listener : listeners) {
            listener.afterModelChange(modelRef, oldValue, newValue);
        }
    }

    private boolean isNil(Object newValue) {
        return NilValue.instance().equals(newValue);
    }


    public void setRemoteServer(AnkorServer remoteServer) {
        this.remoteServer = remoteServer;
    }

    protected void handleLocalChange(ModelRef modelRef, Object oldValue, Object newValue) {
        if (remoteServer != null) {
            LOG.info("sending change to {}: ref={}, old={}, new={}", remoteServer.serverName, modelRef, oldValue, newValue);
            remoteServer.handleRemoteChange(modelRef.toPath(), newValue);
        }
    }

    public void handleLocalAction(ModelRef modelRef, String action) {
        if (remoteServer != null) {
            LOG.info("sending action to {}:  ref={}, action={}", remoteServer.serverName, modelRef, action);
            remoteServer.handleRemoteAction(modelRef.toPath(), action);
        }
    }
}
