package at.irian.ankor.action.method;

import at.irian.ankor.action.ModelAction;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class RemoteMethodAction implements ModelAction {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RemoteMethodAction.class);

    private final String methodExpression;
    private final String resultPath;
    private final boolean autoRefreshActionContext;
    private final ModelAction completeAction;
    private final ModelAction errorAction;

    private RemoteMethodAction(String methodExpression,
                               String resultPath,
                               boolean autoRefreshActionContext,
                               ModelAction completeAction,
                               ModelAction errorAction) {
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

    public RemoteMethodAction onComplete(ModelAction action) {
        return new RemoteMethodAction(methodExpression, resultPath, autoRefreshActionContext, action, errorAction);
    }

    public RemoteMethodAction onError(ModelAction action) {
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

    public ModelAction getCompleteAction() {
        return completeAction;
    }

    public ModelAction getErrorAction() {
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
