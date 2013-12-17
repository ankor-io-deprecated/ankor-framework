package at.irian.ankor.viewmodel.metadata;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class PropertyMetadata {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PropertyMetadata.class);

    private final String propertyName;
    private final Map<Class<?>, Object> genericMetadataMap;

    public PropertyMetadata(String propertyName) {
        this(propertyName, null);
    }

    protected PropertyMetadata(String propertyName, Map<Class<?>, Object> genericMetadataMap) {
        this.propertyName = propertyName;
        this.genericMetadataMap = genericMetadataMap;
    }

    public <T> PropertyMetadata withGenericMetadata(Class<T> metadataType, T metadata) {
        Map<Class<?>, Object> newMap = new HashMap<Class<?>, Object>();
        if (genericMetadataMap != null) {
            newMap.putAll(genericMetadataMap);
        }
        newMap.put(metadataType, metadata);
        return new PropertyMetadata(propertyName, newMap);
    }


    public String getPropertyName() {
        return propertyName;
    }

    @SuppressWarnings("unchecked")
    public <T> T getGenericMetadata(Class<T> metadataType) {
        return (T) genericMetadataMap.get(metadataType);
    }


}
