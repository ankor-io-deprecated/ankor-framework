package at.irian.ankor.core.action.method;

import at.irian.ankor.core.action.ModelAction;
import at.irian.ankor.core.el.SingleReadonlyVariableELContext;
import at.irian.ankor.core.listener.ModelActionListener;
import at.irian.ankor.core.ref.Ref;
import at.irian.ankor.core.ref.RefFactory;
import at.irian.ankor.core.ref.el.ELRefContext;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

/**
* @author MGeiler (Manfred Geiler)
*/
public class RemoteMethodActionListener implements ModelActionListener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RemoteMethodActionListener.class);

    private static final String CONTEXT_VAR_NAME = "context";

    private final ExpressionFactory expressionFactory;
    private final RefFactory refFactory;

    public RemoteMethodActionListener(ExpressionFactory expressionFactory, RefFactory refFactory) {
        this.expressionFactory = expressionFactory;
        this.refFactory = refFactory;
    }

    @Override
    public void handleModelAction(Ref actionContext, ModelAction action) {
        if (action instanceof RemoteMethodAction) {
            processMethodAction(actionContext, (RemoteMethodAction) action);
        }
    }

    private void processMethodAction(Ref actionContext, RemoteMethodAction action) {

        ELContext methodExecutionELContext = createMethodExecutionELContext(actionContext);

        Object result;
        try {
            result = executeMethod(methodExecutionELContext, action.getMethodExpression());
        } catch (Exception e) {
            handleError(actionContext, action, e);
            return;
        }

        handleResult(actionContext, methodExecutionELContext, action.getResultPath(), result);

        if (action.isAutoRefreshActionContext()) {
            actionContext.setValue(actionContext.getValue());
        }

        fireCompleteAction(actionContext, action.getCompleteAction());
    }

    private void handleResult(Ref actionContext,
                              ELContext methodExecutionELContext,
                              String resultPath,
                              Object result) {
        if (resultPath != null) {
            ELRefContext refContext = ((ELRefContext) actionContext.refContext()).with(methodExecutionELContext);
            Ref resultRef = refFactory.ref(resultPath, refContext);
            resultRef.setValue(result);
        }
    }

    private void fireCompleteAction(Ref actionContext, ModelAction completeAction) {
        if (completeAction != null) {
            actionContext.fire(completeAction);
        }
    }

    private void handleError(Ref actionContext, RemoteMethodAction action, Exception e) {
        ModelAction errorAction = action.getErrorAction();
        if (errorAction != null) {
            actionContext.fire(errorAction);
        } else {
            LOG.error("Error executing " + action, e);
        }
    }

    private ELContext createMethodExecutionELContext(Ref actionContext) {
        Object contextValue = actionContext.getValue();
        ELRefContext refContext = (ELRefContext)actionContext.refContext();
        return new SingleReadonlyVariableELContext(refContext.getELContext(),
                                                   CONTEXT_VAR_NAME,
                                                   contextValue);
    }

    private Object executeMethod(ELContext elContext, String methodExpression) {
        ValueExpression ve = expressionFactory.createValueExpression(elContext,
                                                                     "#{" + methodExpression + "}",
                                                                     Object.class);
        return ve.getValue(elContext);
    }

}
