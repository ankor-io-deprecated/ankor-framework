package at.irian.ankor.rmi;

import at.irian.ankor.action.Action;
import at.irian.ankor.event.ActionListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefFactory;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public abstract class RemoteMethodActionListener implements ActionListener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RemoteMethodActionListener.class);

    protected final RefFactory refFactory;

    public RemoteMethodActionListener(
            RefFactory refFactory) {
        this.refFactory = refFactory;
    }

    @Override
    public void processAction(Ref modelContext, Action action) {
        if (action instanceof RemoteMethodAction) {
            processMethodAction(modelContext, (RemoteMethodAction) action);
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
            RefContext refContext = modelContext.refContext();
            Ref resultRef = refFactory.ref(resultPath, refContext.withModelContext(modelContext));
            resultRef.setValue(result);
        }
    }

    private void fireCompleteAction(Ref actionContext, Action completeAction) {
        if (completeAction != null) {
            actionContext.fire(completeAction);
        }
    }

    private void handleError(Ref actionContext, RemoteMethodAction action, Exception e) {
        Action errorAction = action.getErrorAction();
        if (errorAction != null) {
            actionContext.fire(errorAction);
        } else {
            LOG.error("Error executing " + action, e);
        }
    }

    protected abstract Object executeMethod(Ref modelContext, String methodExpression, Map<String, Object> params);
}
