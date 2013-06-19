package at.irian.ankor.core.application;

import at.irian.ankor.core.listener.ListenerRegistry;
import at.irian.ankor.core.listener.ModelChangeListener;
import at.irian.ankor.core.listener.ModelChangeListenerInstance;
import at.irian.ankor.core.ref.Ref;

/**
 * @author Manfred Geiler
 */
public class ModelChangeWatcher {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelChangeWatcher.class);

    private final ListenerRegistry listenerRegistry;

    public ModelChangeWatcher(ListenerRegistry listenerRegistry) {
        this.listenerRegistry = listenerRegistry;
    }

    public void broadcastModelChange(Ref modelRef) {
        for (ModelChangeListenerInstance modelChangeListenerInstance : listenerRegistry.getLocalChangeListenersFor(modelRef)) {
            ModelChangeListener listener = modelChangeListenerInstance.getListener();
            Ref ref = modelChangeListenerInstance.getRef();
            listener.handleModelChange(ref, modelRef);
        }
    }

}


