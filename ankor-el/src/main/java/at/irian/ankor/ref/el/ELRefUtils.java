package at.irian.ankor.ref.el;

import at.irian.ankor.ref.Ref;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

/**
 * @author Manfred Geiler
 */
final class ELRefUtils {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ELRefUtils.class);

    public static final String EXPR_PREFIX = "#{";
    public static final String EXPR_SUFFIX = "}";

    private ELRefUtils() {}

    static Ref rootRef(ELRefContext refContext) {
        return ref(refContext, refContext.getRootPath());
    }

    static Ref ref(ELRefContext refContext, String path) {
        return new ELRef(refContext, createValueExpression(refContext,
                                                           isRootPath(refContext, path)
                                                             ? refContext.getRootPath()
                                                             : path));
    }

    static ValueExpression createValueExpression(ELRefContext refContext, String path) {
        ExpressionFactory expressionFactory = refContext.getExpressionFactory();
        return expressionFactory.createValueExpression(refContext.getELContext(),
                                                       pathToValueExpression(path),
                                                       Object.class);
    }

    static boolean isRootPath(ELRefContext refContext, String path) {
        return path == null || path.isEmpty() || path.equals(refContext.getRootPath());
    }

    static Ref parentRef(ELRef ref) {
        if (ref.isRoot()) {
            return null;
        } else {
            ELRefContext refContext = ref.refContext();
            return ref(refContext, refContext.getPathSyntax().parentOf(ref.path()));
        }
    }

    static Ref subRef(ELRef ref, String subPath) {
        ELRefContext refContext = ref.refContext();
        return ref(refContext, refContext.getPathSyntax().concat(ref.path(), subPath));
    }

    static String path(ELRef elRef) {
        return valueExpressionToPath(elRef.valueExpression().getExpressionString());
    }

    static String pathToValueExpression(String path) {
        return EXPR_PREFIX + path + EXPR_SUFFIX;
    }

    static String valueExpressionToPath(String ve) {
        if (ve.startsWith(EXPR_PREFIX) && ve.endsWith(EXPR_SUFFIX)) {
            return ve.substring(EXPR_PREFIX.length(), ve.length() - EXPR_SUFFIX.length());
        } else {
            throw new IllegalArgumentException("Not a valid value expression: " + ve);
        }
    }

}
