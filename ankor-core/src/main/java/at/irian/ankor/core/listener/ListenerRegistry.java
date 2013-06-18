package at.irian.ankor.core.listener;

import at.irian.ankor.core.ref.ModelRef;
import at.irian.ankor.core.util.CollectionUtils;

import java.util.*;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ListenerRegistry {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ListenerRegistry.class);

    private final Map<ModelRef, Collection<?>> localActionListeners = new HashMap<ModelRef, Collection<?>>();
    private final Map<ModelRef, Collection<?>> remoteActionListeners = new HashMap<ModelRef, Collection<?>>();

    private final Map<ModelRef, Collection<?>> localChangeListeners = new HashMap<ModelRef, Collection<?>>();
    private final Map<ModelRef, Collection<?>> remoteChangeListeners = new HashMap<ModelRef, Collection<?>>();

    public void registerLocalActionListener(ModelRef ref, ModelActionListener listener) {
        addListener(localActionListeners, ref, listener);
    }

    public void registerRemoteActionListener(ModelRef ref, ModelActionListener listener) {
        addListener(remoteActionListeners, ref, listener);
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


    public Collection<ModelActionListener> getRemoteActionListenersFor(ModelRef ref) {
        return getActionListenersFor(remoteActionListeners, ref);
    }

    public Collection<ModelActionListener> getLocalActionListenersFor(ModelRef ref) {
        return getActionListenersFor(localActionListeners, ref);
    }

    public Collection<ModelChangeListenerInstance> getRemoteChangeListenersFor(ModelRef ref) {
        return getChangeListenerInstancesFor(remoteChangeListeners, ref);
    }

    public Collection<ModelChangeListenerInstance> getLocalChangeListenersFor(ModelRef ref) {
        return getChangeListenerInstancesFor(localChangeListeners, ref);
    }

    @SuppressWarnings("unchecked")
    private Collection<ModelActionListener> getActionListenersFor(Map<ModelRef, Collection<?>> map, ModelRef ref) {
        Collection<?> globalListeners = map.get(null);
        Collection<?> normalListeners = map.get(ref);
        return CollectionUtils.concat(globalListeners, normalListeners);
    }

    @SuppressWarnings("unchecked")
    private Collection<ModelChangeListenerInstance> getChangeListenerInstancesFor(Map<ModelRef, Collection<?>> map, final ModelRef ref) {
        Collection result = Collections.emptyList();
        for (Map.Entry<ModelRef, Collection<?>> entry : map.entrySet()) {
            final ModelRef listenerRef = entry.getKey();
            if (listenerRef == null || listenerRef.equals(ref) || listenerRef.isAncestorOf(ref)) {
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
                if (listenersIterator.next() == listener) {   // todo listener instance
                    listenersIterator.remove();
                }
            }
        }
    }

}
