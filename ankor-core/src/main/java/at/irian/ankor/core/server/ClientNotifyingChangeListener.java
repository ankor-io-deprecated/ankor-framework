package at.irian.ankor.core.server;

import at.irian.ankor.core.listener.ModelChangeListener;
import at.irian.ankor.core.ref.ModelRef;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ClientNotifyingChangeListener implements ModelChangeListener {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ClientNotifyingChangeListener.class);

    private final AnkorServer ankorServer;

    public ClientNotifyingChangeListener(AnkorServer ankorServer) {
        this.ankorServer = ankorServer;
    }

    @Override
    public void beforeModelChange(ModelRef modelRef, Object oldValue, Object newValue) {
    }

    @Override
    public void afterModelChange(ModelRef modelRef, Object oldValue, Object newValue) {
        ankorServer.handleServerChange(modelRef, oldValue, newValue);
    }
}
