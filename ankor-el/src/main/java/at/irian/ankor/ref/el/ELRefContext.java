package at.irian.ankor.ref.el;

import at.irian.ankor.el.ModelContextELContext;
import at.irian.ankor.event.ActionNotifier;
import at.irian.ankor.event.ChangeNotifier;
import at.irian.ankor.path.PathSyntax;
import at.irian.ankor.path.el.ELPathSyntax;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;

import javax.el.ELContext;
import javax.el.ExpressionFactory;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ELRefContext implements RefContext {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ELRefContext.class);

    private final ExpressionFactory expressionFactory;
    private final ELContext elContext;
    private final ChangeNotifier changeNotifier;
    private final ActionNotifier actionNotifier;
    private final String modelRootVarName;
    private final String contextVarName;
    private final Ref modelContext;

    public ELRefContext(ExpressionFactory expressionFactory,
                        ELContext elContext,
                        ChangeNotifier changeNotifier,
                        ActionNotifier actionNotifier,
                        String modelRootVarName,
                        String contextVarName, Ref modelContext) {
        this.expressionFactory = expressionFactory;
        this.elContext = elContext;
        this.changeNotifier = changeNotifier;
        this.actionNotifier = actionNotifier;
        this.modelRootVarName = modelRootVarName;
        this.contextVarName = contextVarName;
        this.modelContext = modelContext;
    }

    public ELRefContext with(ELContext elContext) {
        return new ELRefContext(expressionFactory, elContext, changeNotifier, actionNotifier, modelRootVarName,
                                "context", modelContext);
    }

    public ELRefContext withNoModelChangeNotifier() {
        return new ELRefContext(expressionFactory, elContext, null, actionNotifier, modelRootVarName,
                                "context",
                                modelContext);
    }

    @Override
    public ELRefContext withModelContext(Ref modelContext) {
        ModelContextELContext elContext = new ModelContextELContext(this.elContext, contextVarName, modelContext);
        return new ELRefContext(expressionFactory,
                                elContext,
                                changeNotifier,
                                actionNotifier,
                                modelRootVarName, "context", modelContext);
    }

    public ELContext getELContext() {
        return elContext;
    }

    public ChangeNotifier getChangeNotifier() {
        return changeNotifier;
    }

    public ActionNotifier getActionNotifier() {
        return actionNotifier;
    }

    public String getRootPath() {
        return modelRootVarName;
    }

    public ExpressionFactory getExpressionFactory() {
        return expressionFactory;
    }

    @Override
    public Ref getModelContext() {
        return modelContext;
    }

    @Override
    public PathSyntax getPathSyntax() {
        return ELPathSyntax.getInstance();
    }
}