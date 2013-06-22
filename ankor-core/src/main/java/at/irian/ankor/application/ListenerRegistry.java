package at.irian.ankor.application;

import at.irian.ankor.event.ActionListener;
import at.irian.ankor.event.ChangeListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.util.CollectionUtils;

import java.util.*;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ListenerRegistry {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ListenerRegistry.class);

    private final Map<Ref, Collection<ActionListener>> localActionListeners = new HashMap<Ref, Collection<ActionListener>>();
    private final Map<Ref, Collection<ActionListener>> remoteActionListeners = new HashMap<Ref, Collection<ActionListener>>();

    private final Map<Ref, Collection<ChangeListener>> localChangeListeners = new HashMap<Ref, Collection<ChangeListener>>();
    private final Map<Ref, Collection<ChangeListener>> remoteChangeListeners = new HashMap<Ref, Collection<ChangeListener>>();

    public void registerLocalActionListener(Ref actionContext, ActionListener listener) {
        addActionListener(localActionListeners, actionContext, listener);
    }

    public void registerRemoteActionListener(Ref actionContext, ActionListener listener) {
        addActionListener(remoteActionListeners, actionContext, listener);
    }

    private void addActionListener(Map<Ref, Collection<ActionListener>> map,
                                   Ref actionContext,
                                   ActionListener listener) {
        Collection<ActionListener> listeners = map.get(actionContext);
        if (listeners == null) {
            listeners = new ArrayList<ActionListener>();
            map.put(actionContext, listeners);
        }
        listeners.add(listener);
    }

    public Collection<ActionListener> getLocalActionListenersFor(Ref actionContext) {
        return getActionListenersFor(localActionListeners, actionContext);
    }

    public Collection<ActionListener> getRemoteActionListenersFor(Ref actionContext) {
        return getActionListenersFor(remoteActionListeners, actionContext);
    }

    private Collection<ActionListener> getActionListenersFor(Map<Ref, Collection<ActionListener>> map,
                                                             Ref actionContext) {
        Collection<ActionListener> globalListeners = map.get(null);           // global listeners
        Collection<ActionListener> normalListeners = map.get(actionContext);  // action context listeners
        return CollectionUtils.concat(globalListeners, normalListeners);
    }


    public void registerLocalChangeListener(Ref watchedRef, ChangeListener listener) {
        addChangeListener(localChangeListeners, watchedRef, listener);
    }

    public void registerRemoteChangeListener(Ref watchedRef, ChangeListener listener) {
        addChangeListener(remoteChangeListeners, watchedRef, listener);
    }

    private void addChangeListener(Map<Ref, Collection<ChangeListener>> map,
                                   Ref watchedRef, ChangeListener listener) {
        Collection<ChangeListener> listeners = map.get(watchedRef);
        if (listeners == null) {
            listeners = new ArrayList<ChangeListener>();
            map.put(watchedRef, listeners);
        }
        listeners.add(listener);
    }


    public Collection<BoundChangeListener> getLocalChangeListenersFor(Ref changedRef) {
        return getChangeListenerFor(localChangeListeners, changedRef);
    }

    public Collection<BoundChangeListener> getRemoteChangeListenersFor(Ref changedRef) {
        return getChangeListenerFor(remoteChangeListeners, changedRef);
    }

    private Collection<BoundChangeListener> getChangeListenerFor(Map<Ref, Collection<ChangeListener>> map,
                                                                 final Ref changedRef) {
        Collection<BoundChangeListener> result = Collections.emptyList();
        for (Map.Entry<Ref, Collection<ChangeListener>> entry : map.entrySet()) {
            Ref watchedRef = entry.getKey();
            if (watchedRef == null || watchedRef.equals(changedRef) || watchedRef.isDescendantOf(changedRef)) {
                Collection<ChangeListener> listeners = entry.getValue();
                CollectionUtils.Wrapper<ChangeListener, BoundChangeListener> wrapper;
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

    private CollectionUtils.Wrapper<ChangeListener,BoundChangeListener> wrapper(final Ref ref) {
        return new CollectionUtils.Wrapper<ChangeListener,BoundChangeListener>() {
            @Override
            public BoundChangeListener wrap(ChangeListener objToWrap) {
                return new BoundChangeListener(ref, objToWrap);
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
        unregisterAllListenersFor(localActionListeners.keySet(), watchedRef);
        unregisterAllListenersFor(remoteActionListeners.keySet(), watchedRef);
        unregisterAllListenersFor(localChangeListeners.keySet(), watchedRef);
        unregisterAllListenersFor(remoteChangeListeners.keySet(), watchedRef);
    }

    private void unregisterAllListenersFor(Set<Ref> watchedRefs, Ref watchedRef) {
        Iterator<Ref> iterator = watchedRefs.iterator();
        while (iterator.hasNext()) {
            Ref listenerWatchedRef = iterator.next();
            if (listenerWatchedRef == null) {
                if (watchedRef == null) {
                    iterator.remove();
                }
            } else {
                if (listenerWatchedRef.equals(watchedRef) || listenerWatchedRef.isDescendantOf(watchedRef)) {
                    iterator.remove();
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
            if (listeners.isEmpty()) {
                entryIterator.remove();
            }
        }
    }

}
