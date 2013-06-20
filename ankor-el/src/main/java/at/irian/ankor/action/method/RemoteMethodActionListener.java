package at.irian.ankor.action.method;

import at.irian.ankor.action.ModelAction;
import at.irian.ankor.action.ActionListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.ref.el.ELRefContext;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

/**
* @author MGeiler (Manfred Geiler)
*/
public class RemoteMethodActionListener implements ActionListener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RemoteMethodActionListener.class);

    private final ExpressionFactory expressionFactory;
    private final RefFactory refFactory;

    public RemoteMethodActionListener(ExpressionFactory expressionFactory, RefFactory refFactory) {
        this.expressionFactory = expressionFactory;
        this.refFactory = refFactory;
    }

    @Override
    public void processAction(Ref modelContext, ModelAction action) {
        if (action instanceof RemoteMethodAction) {
            processMethodAction(modelContext, (RemoteMethodAction) action);
        }
    }

    private void processMethodAction(Ref modelContext, RemoteMethodAction action) {

        Object result;
        try {
            result = executeMethod(modelContext, action.getMethodExpression());
        } catch (Exception e) {
            handleError(modelContext, action, e);
            return;
        }

        handleResult(modelContext, action.getResultPath(), result);

        if (action.isAutoRefreshActionContext()) {
            modelContext.setValue(modelContext.getValue());
        }

        fireCompleteAction(modelContext, action.getCompleteAction());
    }

    private void handleResult(Ref modelContext, String resultPath, Object result) {
        if (resultPath != null) {
            ELRefContext refContext = (ELRefContext) modelContext.refContext();
            Ref resultRef = refFactory.ref(resultPath, refContext.withModelContext(modelContext));
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

    private Object executeMethod(Ref modelContext, String methodExpression) {
        ELRefContext refContext = (ELRefContext) modelContext.refContext();
        ELContext execELContext = refContext.withModelContext(modelContext).getELContext();
        ValueExpression ve = expressionFactory.createValueExpression(execELContext,
                                                                     "#{" + methodExpression + "}",
                                                                     Object.class);
        return ve.getValue(execELContext);
    }

}
