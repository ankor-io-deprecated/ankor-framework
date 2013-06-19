package at.irian.ankor.core.ref.el;

import at.irian.ankor.core.application.DefaultActionNotifier;
import at.irian.ankor.core.application.DefaultChangeNotifier;
import at.irian.ankor.core.el.ModelContextELContext;
import at.irian.ankor.core.ref.Ref;
import at.irian.ankor.core.ref.RefContext;

import javax.el.ELContext;
import javax.el.ExpressionFactory;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ELRefContext implements RefContext {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ELRefContext.class);

    private final ExpressionFactory expressionFactory;
    private final ELContext elContext;
    private final DefaultChangeNotifier changeNotifier;
    private final DefaultActionNotifier actionNotifier;
    private final String modelRootVarName;
    private final String contextVarName;
    private final Ref modelContext;

    public ELRefContext(ExpressionFactory expressionFactory,
                        ELContext elContext,
                        DefaultChangeNotifier changeNotifier,
                        DefaultActionNotifier actionNotifier,
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

    public DefaultChangeNotifier getChangeNotifier() {
        return changeNotifier;
    }

    public DefaultActionNotifier getActionNotifier() {
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
}
