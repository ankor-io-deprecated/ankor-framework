package at.irian.ankor.core.application;

import at.irian.ankor.core.listener.ListenerRegistry;
import at.irian.ankor.core.listener.ModelChangeListener;
import at.irian.ankor.core.ref.ModelRef;

/**
 * @author Manfred Geiler
 */
public class ModelChangeWatcher {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelChangeWatcher.class);

    private final ListenerRegistry listenerRegistry;

    public ModelChangeWatcher(ListenerRegistry listenerRegistry) {
        this.listenerRegistry = listenerRegistry;
    }

    public void beforeModelChange(ModelRef modelRef, Object oldValue, Object newValue) {
        for (ModelChangeListener modelChangeListener : listenerRegistry.getLocalChangeListenersFor(modelRef)) {
            modelChangeListener.beforeModelChange(modelRef, oldValue, newValue);
        }
    }

    public void afterModelChange(ModelRef modelRef, Object oldValue, Object newValue) {
        for (ModelChangeListener modelChangeListener : listenerRegistry.getLocalChangeListenersFor(modelRef)) {
            modelChangeListener.afterModelChange(modelRef, oldValue, newValue);
        }
    }

}


