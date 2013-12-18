package at.irian.ankor.viewmodel.metadata;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Manfred Geiler
 */
public class BeanMetadata {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BeanMetadata.class);

    public static BeanMetadata EMPTY_BEAN_METADATA = new BeanMetadata();

    private final Collection<ChangeListenerMetadata> changeListeners;
    private final Collection<ActionListenerMetadata> actionListeners;
    private final Map<Method, List<ChangeSignalMetadata>> changeSignals;
    private final Map<String, PropertyMetadata> propertyMetadataMap;

    protected BeanMetadata() {
        this(null, null, null, null);
    }

    protected BeanMetadata(Collection<ChangeListenerMetadata> changeListeners,
                           Collection<ActionListenerMetadata> actionListeners,
                           Map<Method, List<ChangeSignalMetadata>> changeSignals,
                           Map<String, PropertyMetadata> propertyMetadataMap) {
        this.changeListeners = changeListeners;
        this.actionListeners = actionListeners;
        this.changeSignals = changeSignals;
        this.propertyMetadataMap = propertyMetadataMap;
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
            return Collections.unmodifiableCollection(c);
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
        return new BeanMetadata(combine(this.changeListeners, changeListeners), actionListeners,
                                changeSignals, propertyMetadataMap);
    }

    public BeanMetadata withActionListeners(Collection<ActionListenerMetadata> actionListeners) {
        return new BeanMetadata(changeListeners, combine(this.actionListeners, actionListeners),
                                changeSignals, propertyMetadataMap);
    }

    public BeanMetadata withChangeSignals(Collection<ChangeSignalMetadata> changeSignals) {
        if (changeSignals == null || changeSignals.isEmpty()) {
            return this;
        }

        Map<Method, List<ChangeSignalMetadata>> map = new HashMap<Method, List<ChangeSignalMetadata>>();
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

        map = Collections.unmodifiableMap(map);

        return new BeanMetadata(changeListeners, actionListeners, map, propertyMetadataMap);
    }

    public BeanMetadata withPropertyMetadata(String propertyName, Object metadata) {
        Map<String, PropertyMetadata> map = new HashMap<String, PropertyMetadata>();
        if (this.propertyMetadataMap != null) {
            map.putAll(this.propertyMetadataMap);
        }

        PropertyMetadata md = map.get(propertyName);
        if (md == null) {
            md = new PropertyMetadata(propertyName);
        }

        //noinspection unchecked
        md = md.withGenericMetadata((Class<Object>) metadata.getClass(), metadata);

        map.put(propertyName, md);

        map = Collections.unmodifiableMap(map);

        return new BeanMetadata(changeListeners, actionListeners, changeSignals, map);
    }

    public Collection<ChangeListenerMetadata> getChangeListeners() {
        return changeListeners != null ? changeListeners : Collections.<ChangeListenerMetadata>emptyList();
    }

    public Collection<ActionListenerMetadata> getActionListeners() {
        return actionListeners != null ? actionListeners : Collections.<ActionListenerMetadata>emptyList();
    }

    public List<ChangeSignalMetadata> findChangeSignals(Method method) {
        if (changeSignals == null) {
            return Collections.emptyList();
        }
        List<ChangeSignalMetadata> result = changeSignals.get(method);
        return result != null ? result : Collections.<ChangeSignalMetadata>emptyList();
    }

    public PropertyMetadata getPropertyMetadata(String propertyName) {
        if (propertyMetadataMap != null) {
            PropertyMetadata metadata = propertyMetadataMap.get(propertyName);
            if (metadata != null) {
                return metadata;
            }
        }
        return PropertyMetadata.emptyPropertyMetadata(propertyName);
    }

    public Collection<PropertyMetadata> getPropertiesMetadata() {
        if (propertyMetadataMap != null) {
            return propertyMetadataMap.values();
        }
        return Collections.emptyList();
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
