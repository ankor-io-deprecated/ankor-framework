package at.irian.ankor.core.listener;

import at.irian.ankor.core.ref.Ref;
import at.irian.ankor.core.util.CollectionUtils;

import java.util.*;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ListenerRegistry {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ListenerRegistry.class);

    private final Map<Ref, Collection<?>> localActionListeners = new HashMap<Ref, Collection<?>>();
    private final Map<Ref, Collection<?>> remoteActionListeners = new HashMap<Ref, Collection<?>>();

    private final Map<Ref, Collection<?>> localChangeListeners = new HashMap<Ref, Collection<?>>();
    private final Map<Ref, Collection<?>> remoteChangeListeners = new HashMap<Ref, Collection<?>>();

    public void registerLocalActionListener(Ref ref, ModelActionListener listener) {
        addListener(localActionListeners, ref, listener);
    }

    public void registerRemoteActionListener(Ref ref, ModelActionListener listener) {
        addListener(remoteActionListeners, ref, listener);
    }

    public void registerRemoteChangeListener(Ref ref, ModelChangeListener listener) {
        addListener(remoteChangeListeners, ref, listener);
    }

    public void registerLocalChangeListener(Ref ref, ModelChangeListener listener) {
        addListener(localChangeListeners, ref, listener);
    }

    @SuppressWarnings("unchecked")
    private void addListener(Map<Ref, Collection<?>> map, Ref ref, Object listener) {
        Collection listeners = map.get(ref);
        if (listeners == null) {
            listeners = new ArrayList();
            map.put(ref, listeners);
        }
        listeners.add(listener);
    }


    public Collection<ModelActionListener> getRemoteActionListenersFor(Ref ref) {
        return getActionListenersFor(remoteActionListeners, ref);
    }

    public Collection<ModelActionListener> getLocalActionListenersFor(Ref ref) {
        return getActionListenersFor(localActionListeners, ref);
    }

    public Collection<ModelChangeListenerInstance> getRemoteChangeListenersFor(Ref ref) {
        return getChangeListenerInstancesFor(remoteChangeListeners, ref);
    }

    public Collection<ModelChangeListenerInstance> getLocalChangeListenersFor(Ref ref) {
        return getChangeListenerInstancesFor(localChangeListeners, ref);
    }

    @SuppressWarnings("unchecked")
    private Collection<ModelActionListener> getActionListenersFor(Map<Ref, Collection<?>> map, Ref ref) {
        Collection<?> globalListeners = map.get(null);
        Collection<?> normalListeners = map.get(ref);
        return CollectionUtils.concat(globalListeners, normalListeners);
    }

    @SuppressWarnings("unchecked")
    private Collection<ModelChangeListenerInstance> getChangeListenerInstancesFor(Map<Ref, Collection<?>> map, final Ref ref) {
        Collection result = Collections.emptyList();
        for (Map.Entry<Ref, Collection<?>> entry : map.entrySet()) {
            final Ref listenerRef = entry.getKey();
            if (listenerRef == null || listenerRef.equals(ref) || listenerRef.isDescendantOf(ref)) {
                Collection<?> listeners = entry.getValue();
                Collection listenerInstances = CollectionUtils.wrap(listeners, new CollectionUtils.Wrapper() {
                    @Override
                    public Object wrap(Object objToWrap) {
                        return listenerRef != null
                               ? new ModelChangeListenerInstance(listenerRef, (ModelChangeListener) objToWrap)
                               : new ModelChangeListenerInstance(ref.root(), (ModelChangeListener) objToWrap);
                    }
                });
                result = CollectionUtils.concat(result, listenerInstances);
            }
        }
        return result;
    }

    public void unregisterAllListenersFor(Ref ref) {
        unregisterAllListenersFor(remoteActionListeners.entrySet().iterator(), ref);
        unregisterAllListenersFor(localActionListeners.entrySet().iterator(), ref);
        unregisterAllListenersFor(remoteChangeListeners.entrySet().iterator(), ref);
        unregisterAllListenersFor(localChangeListeners.entrySet().iterator(), ref);
    }

    private void unregisterAllListenersFor(Iterator entryIterator, Ref ref) {
        while (entryIterator.hasNext()) {
            Map.Entry entry = (Map.Entry) entryIterator.next();
            Ref listenerRef = (Ref) entry.getKey();
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
                if (listenersIterator.next() == listener) {   // todo listener instance
                    listenersIterator.remove();
                }
            }
        }
    }

}
