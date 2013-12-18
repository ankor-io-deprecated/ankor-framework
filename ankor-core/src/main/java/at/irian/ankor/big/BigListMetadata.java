package at.irian.ankor.big;

/**
 * @author Manfred Geiler
 */
public class BigListMetadata {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BigListMetadata.class);

    /**
     * minimal size of the list for switching to "big list" mode; a value of "-1" means "always" use a BigList
     */
    private final int threshold;

    /**
     * number of list entries that are sent initially when the list is transferred for the first time
     */
    private final int initialSize;

    /**
     * number of list entries that are transferred as a block on each "missing entries request"
     */
    private final int chunkSize;

    /**
     * Java type that may be instantiated (via default constructor) as "missing element substitute"
     */
    private final Class<?> missingElementSubstitute;

    public BigListMetadata(int threshold, int initialSize, int chunkSize, Class<?> missingElementSubstitute) {
        this.threshold = threshold;
        this.initialSize = initialSize;
        this.chunkSize = chunkSize;
        this.missingElementSubstitute = missingElementSubstitute;
    }

    public int getThreshold() {
        return threshold;
    }

    public int getInitialSize() {
        return initialSize;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public Class<?> getMissingElementSubstitute() {
        return missingElementSubstitute;
    }
}
