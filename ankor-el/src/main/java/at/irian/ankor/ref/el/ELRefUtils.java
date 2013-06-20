package at.irian.ankor.ref.el;

import at.irian.ankor.ref.Ref;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import static at.irian.ankor.ref.el.PathUtils.concat;
import static at.irian.ankor.ref.el.PathUtils.parentPath;

/**
 * @author Manfred Geiler
 */
public final class ELRefUtils {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ELRefUtils.class);

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
            return ref(ref.refContext(), parentPath(ref.path()));
        }
    }

    static Ref subRef(ELRef ref, String subPath) {
        return ref(ref.refContext(), concat(ref.path(), subPath));
    }

    static String path(ELRef elRef) {
        return valueExpressionToPath(elRef.valueExpression().getExpressionString());
    }

    public static String pathToValueExpression(String path) {
        return PathUtils.EXPR_PREFIX + path + PathUtils.EXPR_SUFFIX;
    }

    static String valueExpressionToPath(String ve) {
        if (ve.startsWith(PathUtils.EXPR_PREFIX) && ve.endsWith(PathUtils.EXPR_SUFFIX)) {
            return ve.substring(PathUtils.EXPR_PREFIX.length(), ve.length() - PathUtils.EXPR_SUFFIX.length());
        } else {
            throw new IllegalArgumentException("Not a valid value expression: " + ve);
        }
    }

}
