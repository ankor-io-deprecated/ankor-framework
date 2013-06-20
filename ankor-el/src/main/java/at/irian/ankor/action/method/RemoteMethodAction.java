package at.irian.ankor.action.method;

import at.irian.ankor.action.Action;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class RemoteMethodAction implements Action {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RemoteMethodAction.class);

    private final String methodExpression;
    private final String resultPath;
    private final boolean autoRefreshActionContext;
    private final Action completeAction;
    private final Action errorAction;

    private RemoteMethodAction(String methodExpression,
                               String resultPath,
                               boolean autoRefreshActionContext,
                               Action completeAction,
                               Action errorAction) {
        this.methodExpression = methodExpression;
        this.resultPath = resultPath;
        this.autoRefreshActionContext = autoRefreshActionContext;
        this.completeAction = completeAction;
        this.errorAction = errorAction;
    }


    public static RemoteMethodAction create(String methodExpression) {
        return new RemoteMethodAction(methodExpression, null, false, null, null);
    }

    public RemoteMethodAction withActionContextAutoRefresh() {
        return new RemoteMethodAction(methodExpression, resultPath, true, completeAction, errorAction);
    }

    public RemoteMethodAction withResultIn(String resultPath) {
        return new RemoteMethodAction(methodExpression, resultPath, autoRefreshActionContext, completeAction, errorAction);
    }

    public RemoteMethodAction onComplete(Action action) {
        return new RemoteMethodAction(methodExpression, resultPath, autoRefreshActionContext, action, errorAction);
    }

    public RemoteMethodAction onError(Action action) {
        return new RemoteMethodAction(methodExpression, resultPath, autoRefreshActionContext, completeAction, action);
    }

    public String getMethodExpression() {
        return methodExpression;
    }

    @Override
    public String name() {
        return methodExpression;
    }

    public String getResultPath() {
        return resultPath;
    }

    public boolean isAutoRefreshActionContext() {
        return autoRefreshActionContext;
    }

    public Action getCompleteAction() {
        return completeAction;
    }

    public Action getErrorAction() {
        return errorAction;
    }

    @Override
    public String toString() {
        return "RemoteMethodAction{" +
               "methodExpression='" + methodExpression + '\'' +
               ", resultPath='" + resultPath + '\'' +
               ", completeAction=" + completeAction +
               '}';
    }
}
