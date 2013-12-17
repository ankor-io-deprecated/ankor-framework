package at.irian.ankor.viewmodel.metadata;

import java.lang.reflect.Method;

/**
 * @author Manfred Geiler
 */
public class InvocationMetadata {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(InvocationMetadata.class);

    private final Method method;
    private final ParameterMetadata[] parameters;

    public InvocationMetadata(Method method,
                              ParameterMetadata[] parameters) {
        this.method = method;
        this.parameters = parameters;
    }

    public Method getMethod() {
        return method;
    }

    public ParameterMetadata[] getParameters() {
        return parameters;
    }

}
