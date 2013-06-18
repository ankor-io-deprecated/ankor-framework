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
        Collection listeners = map.get(ref);
        Collection globalListeners = map.get(null);
        if (listeners == null && globalListeners == null) {
            return Collections.emptyList();
        } else if (globalListeners == null) {
            return Collections.unmodifiableCollection(listeners);
        } else if (listeners == null) {
            return Collections.unmodifiableCollection(globalListeners);
        } else {
            return new CombinedCollection(globalListeners, listeners);
        }
    }


    public void unregisterAllListenersFor(ModelRef modelRef) {
        unregisterAllListenersFor(remoteActionListeners.entrySet().iterator(), modelRef);
        unregisterAllListenersFor(localActionListeners.entrySet().iterator(), modelRef);
        unregisterAllListenersFor(remoteChangeListeners.entrySet().iterator(), modelRef);
        unregisterAllListenersFor(localChangeListeners.entrySet().iterator(), modelRef);
    }

    public void unregisterListener(Object listener) {
        unregisterListeners(remoteActionListeners.entrySet().iterator(), listener);
        unregisterListeners(localActionListeners.entrySet().iterator(), listener);
        unregisterListeners(remoteChangeListeners.entrySet().iterator(), listener);
        unregisterListeners(localChangeListeners.entrySet().iterator(), listener);
    }


    private void unregisterAllListenersFor(Iterator entryIterator, ModelRef modelRef) {
        while (entryIterator.hasNext()) {
            Map.Entry entry = (Map.Entry) entryIterator.next();
            ModelRef refKey = (ModelRef) entry.getKey();
            while (refKey != null) {
                if (modelRef.equals(refKey)) {
                    entryIterator.remove();
                }
                refKey = refKey.parent();
            }
        }
    }

    private void unregisterListeners(Iterator entryIterator, Object listener) {
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
