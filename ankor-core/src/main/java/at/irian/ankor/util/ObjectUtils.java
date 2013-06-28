package at.irian.ankor.util;

/**
 * @author Manfred Geiler
 */
public final class ObjectUtils {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ObjectUtils.class);

    private ObjectUtils() {}

    public static boolean nullSafeEquals(Object o1, Object o2) {
        return o1 == null && o2 == null || o1 != null && o2 != null && o1.equals(o2);
    }

    public static boolean isEmpty(String name) {
        return name == null || name.trim().isEmpty();
    }
}
