package at.irian.ankor.core.application;

import at.irian.ankor.core.action.ModelAction;
import at.irian.ankor.core.listener.ListenerRegistry;
import at.irian.ankor.core.listener.ModelActionListener;
import at.irian.ankor.core.ref.Ref;

/**
 * @author Manfred Geiler
 */
public class ModelActionBus {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelChangeWatcher.class);

    private final ListenerRegistry listenerRegistry;

    public ModelActionBus(ListenerRegistry listenerRegistry) {
        this.listenerRegistry = listenerRegistry;
    }

    public void broadcastAction(Ref ref, ModelAction action) {
        if (ref == null) {
            throw new NullPointerException("ref");
        }
        if (action == null) {
            throw new NullPointerException("action");
        }
        for (ModelActionListener modelActionListener : listenerRegistry.getLocalActionListenersFor(ref)) {
            modelActionListener.handleModelAction(ref, action);
        }
    }

}


