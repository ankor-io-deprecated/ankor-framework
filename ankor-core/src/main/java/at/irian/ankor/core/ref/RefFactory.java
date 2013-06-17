package at.irian.ankor.core.ref;

import at.irian.ankor.core.application.ModelActionBus;
import at.irian.ankor.core.application.ModelChangeWatcher;
import at.irian.ankor.core.model.ModelHolder;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class RefFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RefFactory.class);

    private static final String EXPR_PREFIX = "#{" + ModelHolderVariableMapper.MODEL_ROOT_VAR_NAME + '.';
    private static final String EXPR_SUFFIX = "}";

    private final ExpressionFactory expressionFactory;
    private final ELContext elContext;
    private final ModelChangeWatcher modelChangeWatcher;
    private final ModelActionBus modelActionBus;
    private final RootRef rootRef;
    private final RootRef unwatchedRootRef;

    public RefFactory(ExpressionFactory expressionFactory,
                      ELContext standardELContext,
                      ModelChangeWatcher modelChangeWatcher,
                      ModelActionBus modelActionBus,
                      ModelHolder modelHolder) {
        this.expressionFactory = expressionFactory;
        this.elContext = new ModelHolderELContext(expressionFactory, standardELContext, modelHolder);
        this.modelChangeWatcher = modelChangeWatcher;
        this.modelActionBus = modelActionBus;
        this.rootRef = new RootRef(this, modelHolder, modelChangeWatcher);
        this.unwatchedRootRef = new RootRef(this, modelHolder, null);
    }

    protected ELContext elContext() {
        return elContext;
    }

    protected ModelActionBus modelActionBus() {
        return modelActionBus;
    }

    public RootRef rootRef() {
        return rootRef;
    }

    protected RootRef unwatchedRootRef() {
        return unwatchedRootRef;
    }

    public ModelRef ref(String path) {
        return ref(path, Object.class);
    }

    public ModelRef ref(String path, Class<?> type) {
        if (path == null || path.isEmpty()) {
            // root reference
            return rootRef;
        } else {
            ValueExpression ve = expressionFactory.createValueExpression(elContext, pathToExpr(path), type);
            return new PropertyRef(this, ve, modelChangeWatcher);
        }
    }

    protected ModelRef unwatchedRef(String path) {
        return unwatchedRef(path, Object.class);
    }

    protected ModelRef unwatchedRef(String path, Class<?> type) {
        if (path == null || path.isEmpty()) {
            // root reference
            return unwatchedRootRef;
        } else {
            ValueExpression ve = expressionFactory.createValueExpression(elContext, pathToExpr(path), type);
            return new PropertyRef(this, ve, null);
        }
    }

    protected ModelRef unwatchedRef(ValueExpression ve) {
        return new PropertyRef(this, ve, null);
    }

    static String pathToExpr(String path) {
        return EXPR_PREFIX + path + EXPR_SUFFIX;
    }

    static String exprToPath(String expr) {
        if (expr.startsWith(EXPR_PREFIX) && expr.endsWith(EXPR_SUFFIX)) {
            return expr.substring(EXPR_PREFIX.length(), expr.length() - EXPR_SUFFIX.length());
        } else {
            throw new IllegalArgumentException("Not a model ref expression: " + expr);
        }
    }
}
