package at.irian.ankor.viewmodel.metadata;

import java.lang.reflect.Field;
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
    private final Map<String, PropertyMetadata> propertyMetadataMap;
    private final Map<Method, MethodMetadata> methodMetadataMap;

    protected BeanMetadata() {
        this(null, null, null, null);
    }

    protected BeanMetadata(Collection<ChangeListenerMetadata> changeListeners,
                           Collection<ActionListenerMetadata> actionListeners,
                           Map<String, PropertyMetadata> propertyMetadataMap,
                           Map<Method, MethodMetadata> methodMetadataMap) {
        this.changeListeners = changeListeners;
        this.actionListeners = actionListeners;
        this.propertyMetadataMap = propertyMetadataMap;
        this.methodMetadataMap = methodMetadataMap;
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

    public BeanMetadata withChangeListeners(Collection<ChangeListenerMetadata> changeListeners) {
        return new BeanMetadata(combine(this.changeListeners, changeListeners), actionListeners,
                                propertyMetadataMap, methodMetadataMap);
    }

    public BeanMetadata withActionListeners(Collection<ActionListenerMetadata> actionListeners) {
        return new BeanMetadata(changeListeners, combine(this.actionListeners, actionListeners),
                                propertyMetadataMap, methodMetadataMap);
    }

    public BeanMetadata withProperty(String propertyName) {
        //noinspection unchecked
        Map<String, PropertyMetadata> map
                = addToPropertyMetadataMap(propertyMetadataMap,
                                           propertyName,
                                           getPropertyMetadata(propertyName));
        return new BeanMetadata(changeListeners, actionListeners, map, methodMetadataMap);
    }

    public BeanMetadata withReadOnlyProperty(String propertyName) {
        //noinspection unchecked
        Map<String, PropertyMetadata> map
                = addToPropertyMetadataMap(propertyMetadataMap,
                                           propertyName,
                                           getPropertyMetadata(propertyName).withReadOnly(true));
        return new BeanMetadata(changeListeners, actionListeners, map, methodMetadataMap);
    }

    public BeanMetadata withVirtualProperty(String propertyName) {
        //noinspection unchecked
        Map<String, PropertyMetadata> map
                = addToPropertyMetadataMap(propertyMetadataMap,
                                           propertyName,
                                           getPropertyMetadata(propertyName).withVirtual(true));
        return new BeanMetadata(changeListeners, actionListeners, map, methodMetadataMap);
    }

    public BeanMetadata withStateHolderProperty(String propertyName) {
        //noinspection unchecked
        Map<String, PropertyMetadata> map
                = addToPropertyMetadataMap(propertyMetadataMap,
                                           propertyName,
                                           getPropertyMetadata(propertyName).withStateHolder(true));
        return new BeanMetadata(changeListeners, actionListeners, map, methodMetadataMap);
    }

    public BeanMetadata withInjectedRefProperty(String propertyName) {
        //noinspection unchecked
        Map<String, PropertyMetadata> map
                = addToPropertyMetadataMap(propertyMetadataMap,
                                           propertyName,
                                           getPropertyMetadata(propertyName).withInjectedRef(true));
        return new BeanMetadata(changeListeners, actionListeners, map, methodMetadataMap);
    }

    public BeanMetadata withTypedProperty(String propertyName, Class<?> type) {
        //noinspection unchecked
        Map<String, PropertyMetadata> map
                = addToPropertyMetadataMap(propertyMetadataMap,
                                           propertyName,
                                           getPropertyMetadata(propertyName).withPropertyType(type));
        return new BeanMetadata(changeListeners, actionListeners, map, methodMetadataMap);
    }

    public BeanMetadata withPropertyField(String propertyName, Field field) {
        //noinspection unchecked
        Map<String, PropertyMetadata> map
                = addToPropertyMetadataMap(propertyMetadataMap,
                                           propertyName,
                                           getPropertyMetadata(propertyName).withField(field));
        return new BeanMetadata(changeListeners, actionListeners, map, methodMetadataMap);
    }

    public BeanMetadata withPropertySetterMethod(String propertyName, Method setterMethod) {
        //noinspection unchecked
        Map<String, PropertyMetadata> map
                = addToPropertyMetadataMap(propertyMetadataMap,
                                           propertyName,
                                           getPropertyMetadata(propertyName).withSetterMethod(setterMethod));
        return new BeanMetadata(changeListeners, actionListeners, map, methodMetadataMap);
    }

    public BeanMetadata withGenericPropertyMetadata(String propertyName, Object genericMetadata) {
        //noinspection unchecked
        Map<String, PropertyMetadata> map
                = addToPropertyMetadataMap(propertyMetadataMap,
                                           propertyName,
                                           getPropertyMetadata(propertyName)
                                                   .withGenericMetadata((Class<Object>) genericMetadata.getClass(),
                                                                        genericMetadata));
        return new BeanMetadata(changeListeners, actionListeners, map, methodMetadataMap);
    }

    private static Map<String, PropertyMetadata> addToPropertyMetadataMap(Map<String, PropertyMetadata> propertyMetadataMap,
                                                                          String propertyName,
                                                                          PropertyMetadata propertyMetadata) {
        Map<String, PropertyMetadata> map = new HashMap<String, PropertyMetadata>();
        if (propertyMetadataMap != null) {
            map.putAll(propertyMetadataMap);
        }
        map.put(propertyName, propertyMetadata);
        map = Collections.unmodifiableMap(map);
        return map;
    }

    public BeanMetadata withGenericMethodMetadata(Method method, Object metadata) {
        Map<Method, MethodMetadata> map = new HashMap<Method, MethodMetadata>();
        if (this.methodMetadataMap != null) {
            map.putAll(this.methodMetadataMap);
        }

        MethodMetadata md = map.get(method);
        if (md == null) {
            md = new MethodMetadata(method);
        }

        //noinspection unchecked
        md = md.withGenericMetadata((Class<Object>) metadata.getClass(), metadata);

        map.put(method, md);

        map = Collections.unmodifiableMap(map);

        return new BeanMetadata(changeListeners, actionListeners, propertyMetadataMap, map);
    }

    public Collection<ChangeListenerMetadata> getChangeListeners() {
        return changeListeners != null ? changeListeners : Collections.<ChangeListenerMetadata>emptyList();
    }

    public Collection<ActionListenerMetadata> getActionListeners() {
        return actionListeners != null ? actionListeners : Collections.<ActionListenerMetadata>emptyList();
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

    public MethodMetadata getMethodMetadata(Method method) {
        if (methodMetadataMap != null) {
            MethodMetadata metadata = methodMetadataMap.get(method);
            if (metadata != null) {
                return metadata;
            }
        }
        return MethodMetadata.emptyMethodMetadata(method);
    }

    public Collection<MethodMetadata> getMethodsMetadata() {
        if (methodMetadataMap != null) {
            return methodMetadataMap.values();
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
