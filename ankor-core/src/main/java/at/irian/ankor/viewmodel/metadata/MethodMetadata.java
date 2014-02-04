package at.irian.ankor.viewmodel.metadata;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class MethodMetadata {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PropertyMetadata.class);

    private final Method method;
    private final Map<Class<?>, Object> genericMetadataMap;

    public MethodMetadata(Method method) {
        this(method, null);
    }

    protected MethodMetadata(Method method, Map<Class<?>, Object> genericMetadataMap) {
        this.method = method;
        this.genericMetadataMap = genericMetadataMap;
    }

    public <T> MethodMetadata withGenericMetadata(Class<T> metadataType, T metadata) {
        Map<Class<?>, Object> newMap = new HashMap<Class<?>, Object>();
        if (genericMetadataMap != null) {
            newMap.putAll(genericMetadataMap);
        }
        newMap.put(metadataType, metadata);
        return new MethodMetadata(method, newMap);
    }

    public Method getMethod() {
        return method;
    }

    @SuppressWarnings("unchecked")
    public <T> T getGenericMetadata(Class<T> metadataType) {
        return genericMetadataMap != null ? (T) genericMetadataMap.get(metadataType) : null;
    }


    public static MethodMetadata emptyMethodMetadata(Method method) {
        return new MethodMetadata(method);
    }

}
