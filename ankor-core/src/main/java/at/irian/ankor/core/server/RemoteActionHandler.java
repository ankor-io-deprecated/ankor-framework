package at.irian.ankor.core.server;

import at.irian.ankor.core.action.CompleteAware;
import at.irian.ankor.core.action.ModelAction;
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

    public void handleRemoteAction(ModelRef actionContext, ModelAction action) {

        // notify listeners
        Collection<ModelActionListener> listeners = listenerRegistry.getRemoteActionListenersFor(actionContext);
        for (ModelActionListener listener : listeners) {
            listener.handleModelAction(actionContext, action);
        }

        if (action instanceof CompleteAware) {
            ((CompleteAware)action).complete(actionContext);
        }
    }

}
