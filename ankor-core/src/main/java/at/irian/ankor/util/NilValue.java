package at.irian.ankor.util;

/**
 * @author Manfred Geiler
 */
public final class NilValue {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(NilValue.class);

    private static final NilValue INSTANCE = new NilValue();

    public static NilValue instance() {
        return INSTANCE;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NilValue;
    }
}
