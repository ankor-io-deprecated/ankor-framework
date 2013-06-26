package at.irian.ankor.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class UnsynchronizedListenersHolder implements ListenersHolder {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(UnsynchronizedListenersHolder.class);

    private final ListenersHolder parentListenersHolder;
    private final List<ModelEventListener> listeners;
    private List<ModelEventListener> listenersCopy = null;

    public UnsynchronizedListenersHolder() {
        this(null);
    }

    public UnsynchronizedListenersHolder(ListenersHolder parentListenersHolder) {
        this.parentListenersHolder = parentListenersHolder;
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
            if (parentListenersHolder != null) {
                listenersCopy.addAll(parentListenersHolder.getListeners());
            }
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

        if (parentListenersHolder != null) {
            parentListenersHolder.cleanupListeners();
        }
    }

}
