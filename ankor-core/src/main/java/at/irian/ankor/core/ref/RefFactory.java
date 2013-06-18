package at.irian.ankor.core.ref;

import at.irian.ankor.core.application.ModelActionBus;
import at.irian.ankor.core.application.ModelChangeWatcher;
import at.irian.ankor.core.model.ModelHolder;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import static at.irian.ankor.core.ref.PathUtils.*;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class RefFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RefFactory.class);

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
        if (path == null || path.isEmpty()) {
            // root reference
            return rootRef;
        } else {
            return ref(path, modelChangeWatcher);
        }
    }

    ModelRef ref(String path, ModelChangeWatcher modelChangeWatcher) {
        ValueExpression ve = valueExpressionFor(prefixPathWithModelBase(path));
        return new PropertyRef(this, ve, modelChangeWatcher);
    }

    private String prefixPathWithModelBase(String path) {
        return ModelHolderVariableMapper.MODEL_ROOT_VAR_NAME + '.' + path;
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
        return new PropertyRef(this, valueExpressionFor(parentPath(internalPathOf(ref))), ref.getModelChangeWatcher());
    }

    ModelRef subRef(PropertyRef ref, String subPath) {
        return new PropertyRef(this,
                               valueExpressionFor(subPath(internalPathOf(ref), subPath)),
                               ref.getModelChangeWatcher());
    }

    private String internalPathOf(PropertyRef ref) {
        ValueExpression valueExpression = ref.getValueExpression();
        return valueExpressionToPath(valueExpression.getExpressionString());
    }

    private String internalPathOf(RootRef ref) {
        return ModelHolderVariableMapper.MODEL_ROOT_VAR_NAME;
    }

    String toString(PropertyRef ref) {
        return "Ref{" + pathOf(ref) + '}';
    }

    String toString(RootRef ref) {
        return "Ref{" + pathOf(ref) + '}';
    }

    String pathOf(PropertyRef ref) {
        return stripRoot(internalPathOf(ref));
    }

    String pathOf(RootRef ref) {
        return "";
    }
}
