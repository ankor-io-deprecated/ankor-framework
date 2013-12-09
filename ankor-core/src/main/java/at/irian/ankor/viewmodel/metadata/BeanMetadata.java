package at.irian.ankor.viewmodel.metadata;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Manfred Geiler
 */
public class BeanMetadata {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BeanMetadata.class);

    private final Collection<ChangeListenerMetadata> changeListeners;
    private final Collection<ActionListenerMetadata> actionListeners;
    private final Collection<WatchedPropertyMetadata> watchedProperties;
    private final Map<Method, List<ChangeSignalMetadata>> changeSignals;

    public BeanMetadata() {
        this(null, null, null, null);
    }

    protected BeanMetadata(Collection<ChangeListenerMetadata> changeListeners,
                           Collection<ActionListenerMetadata> actionListeners,
                           Collection<WatchedPropertyMetadata> watchedProperties,
                           Map<Method, List<ChangeSignalMetadata>> changeSignals) {
        this.changeListeners = changeListeners;
        this.actionListeners = actionListeners;
        this.watchedProperties = watchedProperties;
        this.changeSignals = changeSignals;
    }


    protected static <E> Collection<E> combine(Collection<E> c1, Collection<E> c2) {
        if (c1 == null || c1.isEmpty()) {
            return c2;
        } else if (c2 == null || c2.isEmpty()) {
            return c1;
        } else {
            Collection<E> c = new ArrayList<E>(c1.size() + c2.size());
            c.addAll(c1);
            c.addAll(c2);
            return c;
        }
    }

//    protected static <K,V> Map<K,V> combine(Map<K,V> m1, Map<K,V> m2) {
//        if (m1 == null || m1.isEmpty()) {
//            return m2;
//        } else if (m2 == null || m2.isEmpty()) {
//            return m1;
//        } else {
//            Map<K,V> c = new HashMap<K, V>(m1.size() + m2.size());
//            c.putAll(m1);
//            c.putAll(m2);
//            return c;
//        }
//    }



    public BeanMetadata withChangeListeners(Collection<ChangeListenerMetadata> changeListeners) {
        return new BeanMetadata(combine(this.changeListeners, changeListeners), actionListeners, watchedProperties,
                                changeSignals);
    }

    public BeanMetadata withActionListeners(Collection<ActionListenerMetadata> actionListeners) {
        return new BeanMetadata(changeListeners, combine(this.actionListeners, actionListeners), watchedProperties,
                                changeSignals);
    }

    public BeanMetadata withWatchedProperties(Collection<WatchedPropertyMetadata> watchedProperties) {
        return new BeanMetadata(changeListeners, actionListeners, combine(this.watchedProperties, watchedProperties),
                                changeSignals);
    }

    public BeanMetadata withChangeSignals(Collection<ChangeSignalMetadata> changeSignals) {
        if (changeSignals == null || changeSignals.isEmpty()) {
            return this;
        }

        Map<Method, List<ChangeSignalMetadata>> map
                = new HashMap<Method, List<ChangeSignalMetadata>>();
        if (this.changeSignals != null) {
            map.putAll(this.changeSignals);
        }

        for (ChangeSignalMetadata changeSignal : changeSignals) {
            List<ChangeSignalMetadata> signals = map.get(changeSignal.getMethod());
            if (signals == null) {
                signals = new ArrayList<ChangeSignalMetadata>(1);
                map.put(changeSignal.getMethod(), signals);
            }
            signals.add(changeSignal);
        }

        return new BeanMetadata(changeListeners, actionListeners, watchedProperties, map);
    }



    public Collection<ChangeListenerMetadata> getChangeListeners() {
        return changeListeners != null ? changeListeners : Collections.<ChangeListenerMetadata>emptyList();
    }

    public Collection<ActionListenerMetadata> getActionListeners() {
        return actionListeners != null ? actionListeners : Collections.<ActionListenerMetadata>emptyList();
    }

    public Collection<WatchedPropertyMetadata> getWatchedProperties() {
        return watchedProperties != null ? watchedProperties : Collections.<WatchedPropertyMetadata>emptyList();
    }

    public List<ChangeSignalMetadata> findChangeSignals(Method method) {
        if (changeSignals == null) {
            return Collections.emptyList();
        }
        List<ChangeSignalMetadata> result = changeSignals.get(method);
        return result != null ? result : Collections.<ChangeSignalMetadata>emptyList();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object obj) {
        return (this == obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
