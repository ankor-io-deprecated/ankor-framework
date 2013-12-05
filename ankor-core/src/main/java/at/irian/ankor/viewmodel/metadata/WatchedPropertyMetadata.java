package at.irian.ankor.viewmodel.metadata;

import java.lang.reflect.Field;

/**
 * @author Manfred Geiler
 */
public class WatchedPropertyMetadata {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(WatchedPropertyMetadata.class);

    private final String propertyName;
    private final int diffThreshold;
    private final Field field;

    public WatchedPropertyMetadata(String propertyName, int diffThreshold, Field field) {
        this.propertyName = propertyName;
        this.diffThreshold = diffThreshold;
        this.field = field;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public int getDiffThreshold() {
        return diffThreshold;
    }

    public Field getField() {
        return field;
    }
}
