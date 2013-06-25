package at.irian.ankor.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class UnsynchronizedEventBus implements EventBus {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(UnsynchronizedEventBus.class);

    private final EventBus parentEventBus;
    private final List<ModelEventListener> listeners;
    private List<ModelEventListener> listenersCopy = null;

    public UnsynchronizedEventBus() {
        this(null);
    }

    public UnsynchronizedEventBus(EventBus parentEventBus) {
        this.parentEventBus = parentEventBus;
        this.listeners = new ArrayList<ModelEventListener>();
    }

    @Override
    public void addListener(ModelEventListener listener) {
        if (listeners.add(listener)) {
            listenersCopy = null;
        }
    }

    @Override
    public void removeListener(ModelEventListener listener) {
        if (listeners.remove(listener)) {
            listenersCopy = null;
        }
    }

    @Override
    public List<ModelEventListener> getListeners() {
        if (listenersCopy == null) {
            listenersCopy = new ArrayList<ModelEventListener>(listeners);
        }
        return Collections.unmodifiableList(listenersCopy);
    }

    @Override
    public void cleanupListeners() {
        Iterator<ModelEventListener> it = listeners.iterator();
        boolean changed = false;
        while (it.hasNext()) {
            ModelEventListener listener = it.next();
            if (listener.isDiscardable()) {
                it.remove();
                changed = true;
            }
        }
        if (changed) {
            listenersCopy = null;
        }

        if (parentEventBus != null) {
            parentEventBus.cleanupListeners();
        }
    }

    @Override
    public void fire(ModelEvent event) {
        for (ModelEventListener listener : getListeners()) {
            if (event.isAppropriateListener(listener)) {
                event.processBy(listener);
            }
        }

        if (parentEventBus != null) {
            parentEventBus.fire(event);
        }
    }
}
