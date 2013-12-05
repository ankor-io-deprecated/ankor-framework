package at.irian.ankor.viewmodel.metadata;

import java.lang.reflect.Method;

/**
 * @author Manfred Geiler
 */
public class InvocationMetadata {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(InvocationMetadata.class);

    private final Method method;
    private final ParameterMetadata[] parameters;
    private final Iterable<TouchedPropertyMetadata> touchedProperties;

    public InvocationMetadata(Method method,
                              ParameterMetadata[] parameters,
                              Iterable<TouchedPropertyMetadata> touchedProperties) {
        this.method = method;
        this.parameters = parameters;
        this.touchedProperties = touchedProperties;
    }

    public Method getMethod() {
        return method;
    }

    public ParameterMetadata[] getParameters() {
        return parameters;
    }

    public Iterable<TouchedPropertyMetadata> getTouchedProperties() {
        return touchedProperties;
    }
}
