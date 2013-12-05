package at.irian.ankor.viewmodel.metadata;

/**
 * @author Manfred Geiler
 */
public class TouchedPropertyMetadata {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TouchedPropertyMetadata.class);

    private final String propertyPath;
    private final Class<?> diffHandlerType;
    private final int diffThreshold;

    public TouchedPropertyMetadata(String propertyPath, Class<?> diffHandlerType, int diffThreshold) {
        this.propertyPath = propertyPath;
        this.diffHandlerType = diffHandlerType;
        this.diffThreshold = diffThreshold;
    }

    public String getPropertyPath() {
        return propertyPath;
    }

    public Class<?> getDiffHandlerType() {
        return diffHandlerType;
    }

    public int getDiffThreshold() {
        return diffThreshold;
    }
}
