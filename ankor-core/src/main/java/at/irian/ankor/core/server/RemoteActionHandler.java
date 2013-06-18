package at.irian.ankor.core.server;

import at.irian.ankor.core.listener.ListenerRegistry;
import at.irian.ankor.core.listener.ModelActionListener;
import at.irian.ankor.core.ref.ModelRef;

import java.util.Collection;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class RemoteActionHandler {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RemoteChangeHandler.class);

    private final ListenerRegistry listenerRegistry;

    public RemoteActionHandler(ListenerRegistry listenerRegistry) {
        this.listenerRegistry = listenerRegistry;
    }

    public void handleRemoteAction(ModelRef modelRef, String action) {
        Collection<ModelActionListener> listeners = listenerRegistry.getRemoteActionListenersFor(modelRef);
        for (ModelActionListener listener : listeners) {
            listener.handleModelAction(modelRef, action);
        }
    }

}
