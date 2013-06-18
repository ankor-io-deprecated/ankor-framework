package at.irian.ankor.core.ref;

import at.irian.ankor.core.application.ModelActionBus;
import at.irian.ankor.core.application.ModelChangeWatcher;
import at.irian.ankor.core.application.ModelHolder;
import at.irian.ankor.core.el.ModelHolderELContext;
import at.irian.ankor.core.el.ModelHolderVariableMapper;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import static at.irian.ankor.core.ref.PathUtils.*;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class RefFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RefFactory.class);

    private static final String MODEL_VAR_NAME = ModelHolderVariableMapper.MODEL_VAR_NAME;
    private static final String MODEL_PATH_PREFIX = MODEL_VAR_NAME + '.';

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
        return ref(path, modelChangeWatcher);
    }

    ModelRef ref(String path, ModelChangeWatcher modelChangeWatcher) {
        if (path == null || path.isEmpty() || path.equals(MODEL_VAR_NAME)) {
            // root reference
            return modelChangeWatcher != null ? rootRef : unwatchedRootRef;
        } else {
            ValueExpression ve = valueExpressionFor(prefixPathWithModelBaseIfNecessary(path));
            return new PropertyRef(this, ve, modelChangeWatcher);
        }
    }

    private String prefixPathWithModelBaseIfNecessary(String path) {
        if (path.isEmpty()) {
            return MODEL_VAR_NAME;
        } else if (path.equals(MODEL_VAR_NAME)) {
            return path;
        } else if (path.startsWith(MODEL_PATH_PREFIX)) {
            return path;
        } else {
            return MODEL_PATH_PREFIX + path;
        }
    }

    private ValueExpression valueExpressionFor(String path) {
        return expressionFactory.createValueExpression(elContext,
                                                       pathToValueExpression(path),
                                                       Object.class);
    }

    ModelRef unwatched(PropertyRef ref) {
        if (ref.getModelChangeWatcher() == null) {
            return ref;
        } else {
            return new PropertyRef(this, ref.getValueExpression(), null);
        }
    }

    ModelRef parentRef(PropertyRef ref) {
        String parentPath = parentPath(pathOf(ref));
        if (MODEL_VAR_NAME.equals(parentPath)) {
            return rootRef();
        } else {
            return new PropertyRef(this, valueExpressionFor(parentPath), ref.getModelChangeWatcher());
        }
    }

    ModelRef subRef(PropertyRef ref, String subPath) {
        return new PropertyRef(this,
                               valueExpressionFor(subPath(pathOf(ref), subPath)),
                               ref.getModelChangeWatcher());
    }

    String pathOf(ModelRef ref) {
        if (ref instanceof RootRef) {
            return MODEL_VAR_NAME;
        } else if (ref instanceof PropertyRef) {
            ValueExpression valueExpression = ((PropertyRef)ref).getValueExpression();
            return valueExpressionToPath(valueExpression.getExpressionString());
        } else {
            throw new IllegalArgumentException("unsupported ref type " + ref.getClass());
        }
    }

    String toString(ModelRef ref) {
        return "Ref{" + pathOf(ref) + '}';
    }

}
