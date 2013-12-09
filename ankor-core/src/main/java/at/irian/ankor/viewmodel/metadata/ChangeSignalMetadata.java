package at.irian.ankor.viewmodel.metadata;

import java.lang.reflect.Method;

/**
 * @author Manfred Geiler
 */
public class ChangeSignalMetadata {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TouchedPropertyMetadata.class);

    private final Method method;
    private final String path;

    public ChangeSignalMetadata(Method method, String path) {
        this.method = method;
        this.path = path;
    }

    public Method getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }
}
