package at.irian.ankor.viewmodel.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class PropertyMetadata {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PropertyMetadata.class);

    private final String propertyName;
    private final Class<?> propertyType;
    private final boolean readOnly;
    private final boolean virtual;
    private final boolean stateHolder;
    private final boolean injectedRef;
    private final Field field;
    private final Method setterMethod;
    private final Map<Class<?>, Object> genericMetadataMap;

    protected PropertyMetadata(String propertyName,
                               Class<?> propertyType,
                               boolean readOnly,
                               boolean virtual,
                               boolean stateHolder,
                               boolean injectedRef,
                               Field field,
                               Method setterMethod,
                               Map<Class<?>, Object> genericMetadataMap) {
        this.propertyName = propertyName;
        this.propertyType = propertyType;
        this.readOnly = readOnly;
        this.virtual = virtual;
        this.stateHolder = stateHolder;
        this.injectedRef = injectedRef;
        this.field = field;
        this.setterMethod = setterMethod;
        this.genericMetadataMap = genericMetadataMap;
    }

    public PropertyMetadata withPropertyType(Class<?> propertyType) {
        return new PropertyMetadata(propertyName, propertyType, readOnly, virtual, stateHolder,
                                    injectedRef,
                                    field, setterMethod, genericMetadataMap);
    }

    public PropertyMetadata withReadOnly(boolean readOnly) {
        return new PropertyMetadata(propertyName, propertyType, readOnly, virtual, stateHolder,
                                    injectedRef,
                                    field, setterMethod, genericMetadataMap);
    }

    public PropertyMetadata withVirtual(boolean virtual) {
        return new PropertyMetadata(propertyName, propertyType, readOnly, virtual, stateHolder,
                                    injectedRef,
                                    field, setterMethod, genericMetadataMap);
    }

    public PropertyMetadata withStateHolder(boolean stateHolder) {
        return new PropertyMetadata(propertyName, propertyType, readOnly, virtual, stateHolder,
                                    injectedRef,
                                    field, setterMethod, genericMetadataMap);
    }

    public PropertyMetadata withInjectedRef(boolean injectedRef) {
        return new PropertyMetadata(propertyName, propertyType, readOnly, virtual, stateHolder,
                                    injectedRef,
                                    field, setterMethod, genericMetadataMap);
    }

    public PropertyMetadata withField(Field field) {
        return new PropertyMetadata(propertyName, propertyType, readOnly, virtual, stateHolder,
                                    injectedRef,
                                    field, setterMethod, genericMetadataMap);
    }

    public PropertyMetadata withSetterMethod(Method setterMethod) {
        return new PropertyMetadata(propertyName, propertyType, readOnly, virtual, stateHolder,
                                    injectedRef,
                                    field, setterMethod, genericMetadataMap);
    }

    public <T> PropertyMetadata withGenericMetadata(Class<T> metadataType, T metadata) {
        Map<Class<?>, Object> newMap = new HashMap<Class<?>, Object>();
        if (genericMetadataMap != null) {
            newMap.putAll(genericMetadataMap);
        }
        newMap.put(metadataType, metadata);
        return new PropertyMetadata(propertyName, propertyType, readOnly, virtual, stateHolder, injectedRef,
                                    field, setterMethod,
                                    newMap);
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Class<?> getPropertyType() {
        return propertyType;
    }

    /**
     * todo  needed?
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isVirtual() {
        return virtual;
    }

    public boolean isStateHolder() {
        return stateHolder;
    }

    public boolean isInjectedRef() {
        return injectedRef;
    }

    public Field getField() {
        return field;
    }

    public Method getSetterMethod() {
        return setterMethod;
    }

    @SuppressWarnings("unchecked")
    public <T> T getGenericMetadata(Class<T> metadataType) {
        return genericMetadataMap != null ? (T) genericMetadataMap.get(metadataType) : null;
    }


    public static PropertyMetadata emptyPropertyMetadata(String propertyName) {
        return new PropertyMetadata(propertyName, Object.class, false, false, false, false,
                                    null,
                                    null, Collections.<Class<?>, Object>emptyMap());
    }

}
