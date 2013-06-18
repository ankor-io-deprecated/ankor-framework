package at.irian.ankor.core.server;

import at.irian.ankor.core.listener.ListenerRegistry;
import at.irian.ankor.core.listener.ModelChangeListener;
import at.irian.ankor.core.ref.ModelRef;

import java.util.Collection;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class RemoteChangeHandler {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RemoteChangeHandler.class);

    private final ListenerRegistry listenerRegistry;

    public RemoteChangeHandler(ListenerRegistry listenerRegistry) {
        this.listenerRegistry = listenerRegistry;
    }

    public void handleRemoteChange(ModelRef modelRef, Object newValue) {
        Object oldValue = modelRef.getValue();
        Collection<ModelChangeListener> listeners = listenerRegistry.getRemoteChangeListenersFor(modelRef);

        // notify listeners before local change
        for (ModelChangeListener listener : listeners) {
            listener.beforeModelChange(modelRef, oldValue, newValue);
        }

        // do change model (without notifying local listeners!)
        modelRef.unwatched().setValue(newValue);

        // notify listeners after local change
        for (ModelChangeListener listener : listeners) {
            listener.afterModelChange(modelRef, oldValue, newValue);
        }

        // cleanup listeners if modelRef is unset
        if (isNil(newValue)) {
            listenerRegistry.unregisterAllListenersFor(modelRef);
        }
    }

    private boolean isNil(Object newValue) {
        return NilValue.instance().equals(newValue);
    }

}
