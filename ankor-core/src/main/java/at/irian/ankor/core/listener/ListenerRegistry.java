package at.irian.ankor.core.listener;

import at.irian.ankor.core.ref.ModelRef;

import java.util.*;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ListenerRegistry {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ListenerRegistry.class);

    private final Map<ModelRef, Collection<ModelActionListener>> remoteActionListeners = new HashMap<ModelRef, Collection<ModelActionListener>>();
    private final Map<ModelRef, Collection<ModelChangeListener>> remoteChangeListeners = new HashMap<ModelRef, Collection<ModelChangeListener>>();

    private final Map<ModelRef, Collection<ModelActionListener>> localActionListeners = new HashMap<ModelRef, Collection<ModelActionListener>>();
    private final Map<ModelRef, Collection<ModelChangeListener>> localChangeListeners = new HashMap<ModelRef, Collection<ModelChangeListener>>();

    public void registerRemoteActionListener(ModelRef ref, ModelActionListener listener) {
        Collection<ModelActionListener> listeners = remoteActionListeners.get(ref);
        if (listeners == null) {
            listeners = new ArrayList<ModelActionListener>();
            remoteActionListeners.put(ref, listeners);
        }
        listeners.add(listener);
    }

    public void registerLocalActionListener(ModelRef ref, ModelActionListener listener) {
        Collection<ModelActionListener> listeners = localActionListeners.get(ref);
        if (listeners == null) {
            listeners = new ArrayList<ModelActionListener>();
            localActionListeners.put(ref, listeners);
        }
        listeners.add(listener);
    }

    public void registerRemoteChangeListener(ModelRef ref, ModelChangeListener listener) {
        Collection<ModelChangeListener> listeners = remoteChangeListeners.get(ref);
        if (listeners == null) {
            listeners = new ArrayList<ModelChangeListener>();
            remoteChangeListeners.put(ref, listeners);
        }
        listeners.add(listener);
    }

    public void registerLocalChangeListener(ModelRef ref, ModelChangeListener listener) {
        Collection<ModelChangeListener> listeners = localChangeListeners.get(ref);
        if (listeners == null) {
            listeners = new ArrayList<ModelChangeListener>();
            localChangeListeners.put(ref, listeners);
        }
        listeners.add(listener);
    }

    public Collection<ModelActionListener> getRemoteActionListenersFor(ModelRef ref) {
        Collection<ModelActionListener> listeners = remoteActionListeners.get(ref);
        Collection<ModelActionListener> globalListeners = remoteActionListeners.get(null);
        if (listeners == null && globalListeners == null) {
            return Collections.emptyList();
        } else if (globalListeners == null) {
            return Collections.unmodifiableCollection(listeners);
        } else if (listeners == null) {
            return Collections.unmodifiableCollection(globalListeners);
        } else {
            // todo  optimizer by returning a combining list
            ArrayList<ModelActionListener> result = new ArrayList<ModelActionListener>();
            result.addAll(listeners);
            result.addAll(globalListeners);
            return Collections.unmodifiableCollection(result);
        }
    }

    public Collection<ModelActionListener> getLocalActionListenersFor(ModelRef ref) {
        Collection<ModelActionListener> listeners = localActionListeners.get(ref);
        Collection<ModelActionListener> globalListeners = localActionListeners.get(null);
        if (listeners == null && globalListeners == null) {
            return Collections.emptyList();
        } else if (globalListeners == null) {
            return Collections.unmodifiableCollection(listeners);
        } else if (listeners == null) {
            return Collections.unmodifiableCollection(globalListeners);
        } else {
            // todo  optimizer by returning a combining list
            ArrayList<ModelActionListener> result = new ArrayList<ModelActionListener>();
            result.addAll(listeners);
            result.addAll(globalListeners);
            return Collections.unmodifiableCollection(result);
        }
    }

    public Collection<ModelChangeListener> getRemoteChangeListenersFor(ModelRef ref) {
        Collection<ModelChangeListener> listeners = remoteChangeListeners.get(ref);
        Collection<ModelChangeListener> globalListeners = remoteChangeListeners.get(null);
        if (listeners == null && globalListeners == null) {
            return Collections.emptyList();
        } else if (globalListeners == null) {
            return Collections.unmodifiableCollection(listeners);
        } else if (listeners == null) {
            return Collections.unmodifiableCollection(globalListeners);
        } else {
            // todo  optimizer by returning a combining list
            ArrayList<ModelChangeListener> result = new ArrayList<ModelChangeListener>();
            result.addAll(listeners);
            result.addAll(globalListeners);
            return Collections.unmodifiableCollection(result);
        }
    }

    public Collection<ModelChangeListener> getLocalChangeListenersFor(ModelRef ref) {
        Collection<ModelChangeListener> listeners = localChangeListeners.get(ref);
        Collection<ModelChangeListener> globalListeners = localChangeListeners.get(null);
        if (listeners == null && globalListeners == null) {
            return Collections.emptyList();
        } else if (globalListeners == null) {
            return Collections.unmodifiableCollection(listeners);
        } else if (listeners == null) {
            return Collections.unmodifiableCollection(globalListeners);
        } else {
            // todo  optimizer by returning a combining list
            ArrayList<ModelChangeListener> result = new ArrayList<ModelChangeListener>();
            result.addAll(listeners);
            result.addAll(globalListeners);
            return Collections.unmodifiableCollection(result);
        }
    }

    public void unregisterAllListenersFor(ModelRef modelRef) {
        unregisterAllListenersFor(remoteActionListeners.entrySet().iterator(), modelRef);
        unregisterAllListenersFor(localActionListeners.entrySet().iterator(), modelRef);
        unregisterAllListenersFor(remoteChangeListeners.entrySet().iterator(), modelRef);
        unregisterAllListenersFor(localChangeListeners.entrySet().iterator(), modelRef);
    }

    private void unregisterAllListenersFor(Iterator iterator, ModelRef modelRef) {
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            ModelRef refKey = (ModelRef) entry.getKey();
            while (refKey != null) {
                if (modelRef.equals(refKey)) {
                    iterator.remove();
                }
                refKey = refKey.parent();
            }
        }
    }
}
