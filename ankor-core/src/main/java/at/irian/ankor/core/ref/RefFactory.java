package at.irian.ankor.core.ref;

import at.irian.ankor.core.application.ModelActionBus;
import at.irian.ankor.core.application.ModelChangeNotifier;
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
    private final ModelChangeNotifier modelChangeNotifier;
    private final ModelActionBus modelActionBus;
    private final RootRef rootRef;
    private final RootRef unwatchedRootRef;

    public RefFactory(ExpressionFactory expressionFactory,
                      ELContext standardELContext,
                      ModelChangeNotifier modelChangeNotifier,
                      ModelActionBus modelActionBus,
                      ModelHolder modelHolder) {
        this.expressionFactory = expressionFactory;
        this.elContext = new ModelHolderELContext(expressionFactory, standardELContext, modelHolder);
        this.modelChangeNotifier = modelChangeNotifier;
        this.modelActionBus = modelActionBus;
        this.rootRef = new RootRef(this, modelHolder, modelChangeNotifier);
        this.unwatchedRootRef = new RootRef(this, modelHolder, null);
    }

    protected ELContext elContext() {
        return elContext;
    }

    protected ModelActionBus modelActionBus() {
        return modelActionBus;
    }

    public Ref rootRef() {
        return rootRef;
    }

    protected Ref unwatchedRootRef() {
        return unwatchedRootRef;
    }

    public Ref ref(String path) {
        return ref(path, modelChangeNotifier);
    }

    Ref ref(String path, ModelChangeNotifier modelChangeNotifier) {
        if (path == null || path.isEmpty() || path.equals(MODEL_VAR_NAME)) {
            // root reference
            return modelChangeNotifier != null ? rootRef : unwatchedRootRef;
        } else {
            ValueExpression ve = valueExpressionFor(prefixPathWithModelBaseIfNecessary(path));
            return new PropertyRef(this, ve, modelChangeNotifier);
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

    Ref unwatched(PropertyRef ref) {
        if (ref.getModelChangeNotifier() == null) {
            return ref;
        } else {
            return new PropertyRef(this, ref.getValueExpression(), null);
        }
    }

    Ref parentRef(PropertyRef ref) {
        String parentPath = parentPath(pathOf(ref));
        if (MODEL_VAR_NAME.equals(parentPath)) {
            return rootRef();
        } else {
            return new PropertyRef(this, valueExpressionFor(parentPath), ref.getModelChangeNotifier());
        }
    }

    Ref subRef(PropertyRef ref, String subPath) {
        return new PropertyRef(this,
                               valueExpressionFor(subPath(pathOf(ref), subPath)),
                               ref.getModelChangeNotifier());
    }

    String pathOf(Ref ref) {
        if (ref instanceof RootRef) {
            return MODEL_VAR_NAME;
        } else if (ref instanceof PropertyRef) {
            ValueExpression valueExpression = ((PropertyRef)ref).getValueExpression();
            return valueExpressionToPath(valueExpression.getExpressionString());
        } else {
            throw new IllegalArgumentException("unsupported ref type " + ref.getClass());
        }
    }

    String toString(Ref ref) {
        return "Ref{" + pathOf(ref) + '}';
    }

}
