package at.irian.ankor.core.listener;

import at.irian.ankor.core.ref.ModelRef;
import at.irian.ankor.core.util.CombinedCollection;

import java.util.*;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ListenerRegistry {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ListenerRegistry.class);

    private final Map<ModelRef, Collection<?>> remoteActionListeners = new HashMap<ModelRef, Collection<?>>();
    private final Map<ModelRef, Collection<?>> remoteChangeListeners = new HashMap<ModelRef, Collection<?>>();

    private final Map<ModelRef, Collection<?>> localActionListeners = new HashMap<ModelRef, Collection<?>>();
    private final Map<ModelRef, Collection<?>> localChangeListeners = new HashMap<ModelRef, Collection<?>>();

    public void registerRemoteActionListener(ModelRef ref, ModelActionListener listener) {
        addListener(remoteActionListeners, ref, listener);
    }

    public void registerLocalActionListener(ModelRef ref, ModelActionListener listener) {
        addListener(localActionListeners, ref, listener);
    }

    public void registerRemoteChangeListener(ModelRef ref, ModelChangeListener listener) {
        addListener(remoteChangeListeners, ref, listener);
    }

    public void registerLocalChangeListener(ModelRef ref, ModelChangeListener listener) {
        addListener(localChangeListeners, ref, listener);
    }

    @SuppressWarnings("unchecked")
    private void addListener(Map<ModelRef, Collection<?>> map, ModelRef ref, Object listener) {
        Collection listeners = map.get(ref);
        if (listeners == null) {
            listeners = new ArrayList();
            map.put(ref, listeners);
        }
        listeners.add(listener);
    }


    @SuppressWarnings("unchecked")
    public Collection<ModelActionListener> getRemoteActionListenersFor(ModelRef ref) {
        return getListenersFor(remoteActionListeners, ref);
    }

    @SuppressWarnings("unchecked")
    public Collection<ModelActionListener> getLocalActionListenersFor(ModelRef ref) {
        return getListenersFor(localActionListeners, ref);
    }

    @SuppressWarnings("unchecked")
    public Collection<ModelChangeListener> getRemoteChangeListenersFor(ModelRef ref) {
        return getListenersFor(remoteChangeListeners, ref);
    }

    @SuppressWarnings("unchecked")
    public Collection<ModelChangeListener> getLocalChangeListenersFor(ModelRef ref) {
        return getListenersFor(localChangeListeners, ref);
    }

    private Collection getListenersFor(Map<ModelRef, Collection<?>> map, ModelRef ref) {
        Collection result = Collections.emptyList();
        for (Map.Entry<ModelRef, Collection<?>> entry : map.entrySet()) {
            ModelRef listenerRef = entry.getKey();
            if (listenerRef == null || listenerRef.equals(ref) || listenerRef.isAncestorOf(ref)) {
                result = CombinedCollection.combine(result, entry.getValue());
            }
        }
        return result;
    }

    public void unregisterAllListenersFor(ModelRef modelRef) {
        unregisterAllListenersFor(remoteActionListeners.entrySet().iterator(), modelRef);
        unregisterAllListenersFor(localActionListeners.entrySet().iterator(), modelRef);
        unregisterAllListenersFor(remoteChangeListeners.entrySet().iterator(), modelRef);
        unregisterAllListenersFor(localChangeListeners.entrySet().iterator(), modelRef);
    }

    private void unregisterAllListenersFor(Iterator entryIterator, ModelRef ref) {
        while (entryIterator.hasNext()) {
            Map.Entry entry = (Map.Entry) entryIterator.next();
            ModelRef listenerRef = (ModelRef) entry.getKey();
            if (listenerRef != null && (listenerRef.equals(ref) || listenerRef.isDescendantOf(ref))) {
                entryIterator.remove();
            }
        }
    }

    public void unregisterListener(Object listener) {
        unregisterListener(remoteActionListeners.entrySet().iterator(), listener);
        unregisterListener(localActionListeners.entrySet().iterator(), listener);
        unregisterListener(remoteChangeListeners.entrySet().iterator(), listener);
        unregisterListener(localChangeListeners.entrySet().iterator(), listener);
    }

    private void unregisterListener(Iterator entryIterator, Object listener) {
        while (entryIterator.hasNext()) {
            Map.Entry entry = (Map.Entry) entryIterator.next();
            Collection listeners = (Collection) entry.getValue();
            Iterator listenersIterator = listeners.iterator();
            while (listenersIterator.hasNext()) {
                if (listenersIterator.next() == listener) {
                    listenersIterator.remove();
                }
            }
        }
    }

}
