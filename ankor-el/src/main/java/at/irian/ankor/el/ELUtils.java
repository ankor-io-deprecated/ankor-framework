package at.irian.ankor.el;

/**
 * @author Manfred Geiler
 */
public final class ELUtils {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ELUtils.class);

    public static final String EXPR_PREFIX = "#{";
    public static final String EXPR_SUFFIX = "}";

    public static String pathToExpr(String path) {
        return EXPR_PREFIX + path + EXPR_SUFFIX;
    }

    public static String exprToPath(String expr) {
        if (expr.startsWith(EXPR_PREFIX) && expr.endsWith(EXPR_SUFFIX)) {
            return expr.substring(EXPR_PREFIX.length(), expr.length() - EXPR_SUFFIX.length());
        } else {
            throw new IllegalArgumentException("Not a valid EL expression: " + expr);
        }
    }

}
