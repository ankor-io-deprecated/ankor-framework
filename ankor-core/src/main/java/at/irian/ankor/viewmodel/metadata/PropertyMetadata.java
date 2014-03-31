package at.irian.ankor.viewmodel.metadata;

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
    private final Map<Class<?>, Object> genericMetadataMap;

    protected PropertyMetadata(String propertyName,
                               Class<?> propertyType,
                               boolean readOnly,
                               boolean virtual,
                               Map<Class<?>, Object> genericMetadataMap) {
        this.propertyName = propertyName;
        this.propertyType = propertyType;
        this.readOnly = readOnly;
        this.virtual = virtual;
        this.genericMetadataMap = genericMetadataMap;
    }

    public PropertyMetadata withPropertyType(Class<?> propertyType) {
        return new PropertyMetadata(propertyName, propertyType, readOnly, virtual, genericMetadataMap);
    }

    public PropertyMetadata withReadOnly(boolean readOnly) {
        return new PropertyMetadata(propertyName, propertyType, readOnly, virtual, genericMetadataMap);
    }

    public PropertyMetadata withVirtual(boolean virtual) {
        return new PropertyMetadata(propertyName, propertyType, readOnly, virtual, genericMetadataMap);
    }

    public <T> PropertyMetadata withGenericMetadata(Class<T> metadataType, T metadata) {
        Map<Class<?>, Object> newMap = new HashMap<Class<?>, Object>();
        if (genericMetadataMap != null) {
            newMap.putAll(genericMetadataMap);
        }
        newMap.put(metadataType, metadata);
        return new PropertyMetadata(propertyName, propertyType, readOnly, virtual, newMap);
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Class<?> getPropertyType() {
        return propertyType;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isVirtual() {
        return virtual;
    }

    @SuppressWarnings("unchecked")
    public <T> T getGenericMetadata(Class<T> metadataType) {
        return genericMetadataMap != null ? (T) genericMetadataMap.get(metadataType) : null;
    }


    public static PropertyMetadata emptyPropertyMetadata(String propertyName) {
        return new PropertyMetadata(propertyName, Object.class, false, false, Collections.<Class<?>, Object>emptyMap());
    }

}
