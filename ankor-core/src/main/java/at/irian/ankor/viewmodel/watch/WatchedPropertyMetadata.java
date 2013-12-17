package at.irian.ankor.viewmodel.watch;

import java.lang.reflect.Field;

/**
 * @author Manfred Geiler
 */
public class WatchedPropertyMetadata {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(WatchedPropertyMetadata.class);

    private final int diffThreshold;
    private final Field field;

    public WatchedPropertyMetadata(int diffThreshold, Field field) {
        this.diffThreshold = diffThreshold;
        this.field = field;
    }

    public int getDiffThreshold() {
        return diffThreshold;
    }

    public Field getField() {
        return field;
    }
}
