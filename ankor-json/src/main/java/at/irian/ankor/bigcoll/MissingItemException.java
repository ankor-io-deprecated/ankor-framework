package at.irian.ankor.bigcoll;

/**
 * @author Manfred Geiler
 */
public class MissingItemException extends RuntimeException {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MissingItemException.class);

    public MissingItemException(String message) {
        super(message);
    }
}
