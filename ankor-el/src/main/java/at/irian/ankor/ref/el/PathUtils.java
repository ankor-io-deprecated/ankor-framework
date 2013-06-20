package at.irian.ankor.ref.el;

/**
 * @author Manfred Geiler
 */
public final class PathUtils {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PathUtils.class);

    public static final String EXPR_PREFIX = "#{";
    public static final String EXPR_SUFFIX = "}";

    private PathUtils() {}

    public static String parentPath(String path) {
        if (path.endsWith("]")) {
            int i = path.lastIndexOf('[');
            if (i >= 0) {
                return path.substring(0, i);
            }
        } else {
            int i = path.lastIndexOf('.');
            if (i >= 0) {
                return path.substring(0, i);
            }
        }
        throw new IllegalArgumentException("Not a valid path: " + path);
    }

    public static String concat(String path, String subPath) {
        return path + '.' + subPath;
    }

}
