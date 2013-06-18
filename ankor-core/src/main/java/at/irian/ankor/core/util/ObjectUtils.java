package at.irian.ankor.core.util;

/**
 * @author MGeiler (Manfred Geiler)
 */
public final class ObjectUtils {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ObjectUtils.class);

    private ObjectUtils() {}

    public static boolean equals(Object o1, Object o2) {
        return o1 == null && o2 == null || o1 != null && o2 != null && o1.equals(o2);
    }

}
