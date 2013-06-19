package at.irian.ankor.core.listener;

import at.irian.ankor.core.ref.Ref;
import at.irian.ankor.core.util.CollectionUtils;

import java.util.*;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ListenerRegistry {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ListenerRegistry.class);

    private final Map<Ref, Collection<ModelActionListener>> localActionListeners = new HashMap<Ref, Collection<ModelActionListener>>();
    private final Map<Ref, Collection<ModelActionListener>> remoteActionListeners = new HashMap<Ref, Collection<ModelActionListener>>();

    private final Map<Ref, Collection<ModelChangeListener>> localChangeListeners = new HashMap<Ref, Collection<ModelChangeListener>>();
    private final Map<Ref, Collection<ModelChangeListener>> remoteChangeListeners = new HashMap<Ref, Collection<ModelChangeListener>>();

    public void registerLocalActionListener(Ref actionContextRef, ModelActionListener listener) {
        addActionListener(localActionListeners, actionContextRef, listener);
    }

    public void registerRemoteActionListener(Ref actionContextRef, ModelActionListener listener) {
        addActionListener(remoteActionListeners, actionContextRef, listener);
    }

    private void addActionListener(Map<Ref, Collection<ModelActionListener>> map,
                                   Ref actionContextRef, ModelActionListener listener) {
        Collection<ModelActionListener> listeners = map.get(actionContextRef);
        if (listeners == null) {
            listeners = new ArrayList<ModelActionListener>();
            map.put(actionContextRef, listeners);
        }
        listeners.add(listener);
    }

    public Collection<ModelActionListener> getLocalActionListenersFor(Ref actionContextRef) {
        return getActionListenersFor(localActionListeners, actionContextRef);
    }

    public Collection<ModelActionListener> getRemoteActionListenersFor(Ref actionContextRef) {
        return getActionListenersFor(remoteActionListeners, actionContextRef);
    }

    private Collection<ModelActionListener> getActionListenersFor(Map<Ref, Collection<ModelActionListener>> map, Ref actionContextRef) {
        Collection<ModelActionListener> globalListeners = map.get(null);              // global listeners
        Collection<ModelActionListener> normalListeners = map.get(actionContextRef);  // action context listeners
        return CollectionUtils.concat(globalListeners, normalListeners);
    }


    public void registerLocalChangeListener(Ref watchedRef, ModelChangeListener listener) {
        addChangeListener(localChangeListeners, watchedRef, listener);
    }

    public void registerRemoteChangeListener(Ref watchedRef, ModelChangeListener listener) {
        addChangeListener(remoteChangeListeners, watchedRef, listener);
    }

    private void addChangeListener(Map<Ref, Collection<ModelChangeListener>> map,
                                   Ref watchedRef, ModelChangeListener listener) {
        Collection<ModelChangeListener> listeners = map.get(watchedRef);
        if (listeners == null) {
            listeners = new ArrayList<ModelChangeListener>();
            map.put(watchedRef, listeners);
        }
        listeners.add(listener);
    }


    public Collection<ModelChangeListenerInstance> getLocalChangeListenersFor(Ref changedRef) {
        return getChangeListenerInstancesFor(localChangeListeners, changedRef);
    }

    public Collection<ModelChangeListenerInstance> getRemoteChangeListenersFor(Ref changedRef) {
        return getChangeListenerInstancesFor(remoteChangeListeners, changedRef);
    }

    private Collection<ModelChangeListenerInstance> getChangeListenerInstancesFor(Map<Ref, Collection<ModelChangeListener>> map,
                                                                                  final Ref changedRef) {
        Collection<ModelChangeListenerInstance> result = Collections.emptyList();
        for (Map.Entry<Ref, Collection<ModelChangeListener>> entry : map.entrySet()) {
            Ref watchedRef = entry.getKey();
            if (watchedRef == null || watchedRef.equals(changedRef) || watchedRef.isDescendantOf(changedRef)) {
                Collection<ModelChangeListener> listeners = entry.getValue();
                CollectionUtils.Wrapper<ModelChangeListener, ModelChangeListenerInstance> wrapper;
                if (watchedRef == null) {
                    wrapper = wrapper(changedRef.root());
                } else {
                    wrapper = wrapper(watchedRef);
                }
                result = CollectionUtils.concat(result, CollectionUtils.wrap(listeners, wrapper));
            }
        }
        return result;
    }

    private CollectionUtils.Wrapper<ModelChangeListener,ModelChangeListenerInstance> wrapper(final Ref ref) {
        return new CollectionUtils.Wrapper<ModelChangeListener,ModelChangeListenerInstance>() {
            @Override
            public ModelChangeListenerInstance wrap(ModelChangeListener objToWrap) {
                return new ModelChangeListenerInstance(ref, objToWrap);
            }
        };
    }



    public void unregisterAllListeners() {
        localActionListeners.clear();
        remoteActionListeners.clear();
        localChangeListeners.clear();
        remoteChangeListeners.clear();
    }

    public void unregisterAllListenersFor(Ref watchedRef) {
        unregisterAllListenersFor(localActionListeners.entrySet().iterator(), watchedRef);
        unregisterAllListenersFor(remoteActionListeners.entrySet().iterator(), watchedRef);
        unregisterAllListenersFor(localChangeListeners.entrySet().iterator(), watchedRef);
        unregisterAllListenersFor(remoteChangeListeners.entrySet().iterator(), watchedRef);
    }

    private void unregisterAllListenersFor(Iterator entryIterator, Ref watchedRef) {
        while (entryIterator.hasNext()) {
            Map.Entry entry = (Map.Entry) entryIterator.next();
            Ref listenerWatchedRef = (Ref) entry.getKey();
            if (listenerWatchedRef == null) {
                if (watchedRef == null) {
                    entryIterator.remove();
                }
            } else {
                if (listenerWatchedRef.equals(watchedRef) || listenerWatchedRef.isDescendantOf(watchedRef)) {
                    entryIterator.remove();
                }
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
