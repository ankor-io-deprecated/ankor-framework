package at.irian.ankor.rmi;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public abstract class RemoteMethodActionEventListener extends ActionEvent.Listener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RemoteMethodActionEventListener.class);

    protected RemoteMethodActionEventListener() {
        super(null); // always global
    }

    @Override
    public void processAction(Ref sourceProperty, Action action) {
        if (action instanceof RemoteMethodAction) {
            processMethodAction(sourceProperty, (RemoteMethodAction) action);
        }
    }

    private void processMethodAction(Ref modelContext, RemoteMethodAction action) {

        Object result;
        try {
            result = executeMethod(modelContext, action.getMethodExpression(), action.getParams());
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
            RefContext refContext = modelContext.context();
            Ref resultRef = refContext.refFactory().ref(resultPath);
            resultRef.setValue(result);
        }
    }

    private void fireCompleteAction(Ref actionContext, Action completeAction) {
        if (completeAction != null) {
            actionContext.fireAction(completeAction);
        }
    }

    private void handleError(Ref actionContext, RemoteMethodAction action, Exception e) {
        Action errorAction = action.getErrorAction();
        if (errorAction != null) {
            actionContext.fireAction(errorAction);
        } else {
            LOG.error("Error executing " + action, e);
        }
    }

    protected abstract Object executeMethod(Ref modelContext, String methodExpression, Map<String, Object> params);
}
