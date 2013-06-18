package at.irian.ankor.core.application;

import at.irian.ankor.core.action.ModelAction;
import at.irian.ankor.core.listener.ListenerRegistry;
import at.irian.ankor.core.listener.ModelActionListener;
import at.irian.ankor.core.ref.ModelRef;

/**
 * @author Manfred Geiler
 */
public class ModelActionBus {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelChangeWatcher.class);

    private final ListenerRegistry listenerRegistry;

    public ModelActionBus(ListenerRegistry listenerRegistry) {
        this.listenerRegistry = listenerRegistry;
    }

    public void broadcastAction(ModelRef modelRef, ModelAction action) {
        for (ModelActionListener modelActionListener : listenerRegistry.getLocalActionListenersFor(modelRef)) {
            modelActionListener.handleModelAction(modelRef, action);
        }
    }

}


