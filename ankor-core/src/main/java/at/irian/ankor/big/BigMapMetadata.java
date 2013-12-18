package at.irian.ankor.big;

/**
 * @author Manfred Geiler
 */
public class BigMapMetadata {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BigMapMetadata.class);

    /**
     * minimal size of the map for switching to "big map" mode; a value of "-1" means "always" use a BigMap
     */
    private final int threshold;

    /**
     * number of map entries that are sent initially when the map is transferred for the first time
     */
    private final int initialSize;

    /**
     * Java type that may be instantiated (via default constructor) as "missing value substitute"
     */
    private final Class<?> missingValueSubstitute;

    public BigMapMetadata(int threshold, int initialSize, Class<?> missingValueSubstitute) {
        this.threshold = threshold;
        this.initialSize = initialSize;
        this.missingValueSubstitute = missingValueSubstitute;
    }

    public int getThreshold() {
        return threshold;
    }

    public int getInitialSize() {
        return initialSize;
    }

    public Class<?> getMissingValueSubstitute() {
        return missingValueSubstitute;
    }
}
