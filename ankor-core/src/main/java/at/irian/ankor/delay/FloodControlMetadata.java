package at.irian.ankor.delay;

/**
 * @author Manfred Geiler
 */
public class FloodControlMetadata {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(FloodControlMetadata.class);

    private final long delayMillis;

    public FloodControlMetadata(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    public long getDelayMillis() {
        return delayMillis;
    }
}
