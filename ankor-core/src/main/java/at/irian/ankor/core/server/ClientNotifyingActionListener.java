package at.irian.ankor.core.server;

import at.irian.ankor.core.listener.ModelActionListener;
import at.irian.ankor.core.ref.ModelRef;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ClientNotifyingActionListener implements ModelActionListener {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ClientNotifyingChangeListener.class);

    private final AnkorServer ankorServer;

    public ClientNotifyingActionListener(AnkorServer ankorServer) {
        this.ankorServer = ankorServer;
    }

    @Override
    public void handleModelAction(ModelRef modelRef, String action) {
        ankorServer.handleLocalAction(modelRef, action);
    }
}
