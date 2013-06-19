package at.irian.ankor.core.ref.el;

import at.irian.ankor.core.application.DefaultActionNotifier;
import at.irian.ankor.core.application.DefaultChangeNotifier;
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

    public ELRefContext(ExpressionFactory expressionFactory,
                        ELContext elContext,
                        DefaultChangeNotifier changeNotifier,
                        DefaultActionNotifier actionNotifier,
                        String modelRootVarName) {
        this.expressionFactory = expressionFactory;
        this.elContext = elContext;
        this.changeNotifier = changeNotifier;
        this.actionNotifier = actionNotifier;
        this.modelRootVarName = modelRootVarName;
    }

    public ELRefContext with(ELContext elContext) {
        return new ELRefContext(expressionFactory, elContext, changeNotifier, actionNotifier, modelRootVarName);
    }

    public ELRefContext withNoModelChangeNotifier() {
        return new ELRefContext(expressionFactory, elContext, null, actionNotifier, modelRootVarName);
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
}
