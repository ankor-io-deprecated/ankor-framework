package at.irian.ankor.viewmodel.metadata;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class PropertyMetadata {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PropertyMetadata.class);

    private final String propertyName;
    private final Map<String, Object> genericMetadataMap;

    public PropertyMetadata(String propertyName) {
        this(propertyName, null);
    }

    protected PropertyMetadata(String propertyName, Map<String, Object> genericMetadataMap) {
        this.propertyName = propertyName;
        this.genericMetadataMap = genericMetadataMap;
    }

    public PropertyMetadata withGenericMetadata(String metadataKey, Object metadata) {
        Map<String, Object> newMap = new HashMap<String, Object>();
        if (genericMetadataMap != null) {
            newMap.putAll(genericMetadataMap);
        }
        newMap.put(metadataKey, metadata);
        return new PropertyMetadata(propertyName, newMap);
    }


    public String getPropertyName() {
        return propertyName;
    }

    @SuppressWarnings("unchecked")
    public <T> T getGenericMetadata(String metadataKey) {
        return (T) genericMetadataMap.get(metadataKey);
    }


}
