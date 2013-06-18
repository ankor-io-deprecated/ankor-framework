package at.irian.ankor.core.ref;

import javax.el.ValueExpression;

/**
 * @author Manfred Geiler
 */
public final class PathUtils {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PathUtils.class);

    public static final String EXPR_PREFIX = "#{";
    public static final String EXPR_SUFFIX = "}";

    private PathUtils() {}

    public static String pathToValueExpression(String path) {
        return EXPR_PREFIX + path + EXPR_SUFFIX;
    }

    public static String valueExpressionToPath(String expr) {
        if (expr.startsWith(EXPR_PREFIX) && expr.endsWith(EXPR_SUFFIX)) {
            return expr.substring(EXPR_PREFIX.length(), expr.length() - EXPR_SUFFIX.length());
        } else {
            throw new IllegalArgumentException("Not a path expression: " + expr);
        }
    }

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

    public static String parentPath(ValueExpression ve) {
        String expr = ve.getExpressionString();
        String path = valueExpressionToPath(expr);
        return parentPath(path);
    }

    public static String subPath(String path, String subPath) {
        return path + '.' + subPath;
    }

    public static String subPath(String path, int arrayIndex) {
        return path + '[' + arrayIndex + ']';
    }

    public static String stripRoot(String path) {
        int i = path.indexOf('.');
        if (i >= 0) {
            return path.substring(i + 1);
        }
        throw new IllegalArgumentException("Not a valid path: " + path);
    }

}
