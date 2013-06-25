package at.irian.ankor.rmi;

import at.irian.ankor.action.Action;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class RemoteMethodAction implements Action {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RemoteMethodAction.class);

    private String methodExpression;
    private String resultPath;
    private boolean autoRefreshActionContext;
    private Action completeAction;
    private Action errorAction;
    private Map<String, Object> params;

    /**
     * for deserialization only
     */
    @SuppressWarnings("UnusedDeclaration")
    protected RemoteMethodAction() {}

    private RemoteMethodAction(String methodExpression,
                               String resultPath,
                               boolean autoRefreshActionContext,
                               Action completeAction,
                               Action errorAction,
                               Map<String, Object> params) {
        this.methodExpression = methodExpression;
        this.resultPath = resultPath;
        this.autoRefreshActionContext = autoRefreshActionContext;
        this.completeAction = completeAction;
        this.errorAction = errorAction;
        this.params = params;
    }

    public static RemoteMethodAction create(String methodExpression) {
        return new RemoteMethodAction(methodExpression, null, false, null, null, null);
    }

    public RemoteMethodAction withActionContextAutoRefresh() {
        return new RemoteMethodAction(methodExpression, resultPath, true,
                                      completeAction, errorAction, params);
    }

    public RemoteMethodAction withResultIn(String resultPath) {
        return new RemoteMethodAction(methodExpression, resultPath, autoRefreshActionContext,
                                      completeAction, errorAction, params);
    }

    public RemoteMethodAction onComplete(Action action) {
        return new RemoteMethodAction(methodExpression, resultPath, autoRefreshActionContext,
                                      action, errorAction, params);
    }

    public RemoteMethodAction onError(Action action) {
        return new RemoteMethodAction(methodExpression, resultPath, autoRefreshActionContext,
                                      completeAction, action, params);
    }

    public RemoteMethodAction setParam(String name, Object value) {
        Map<String, Object> newParams = new HashMap<String, Object>();
        if (params != null) {
            newParams.putAll(params);
        }
        newParams.put(name, value);
        return new RemoteMethodAction(methodExpression, resultPath, autoRefreshActionContext,
                                      completeAction,
                                      errorAction,
                                      newParams);
    }

    public String getMethodExpression() {
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

    public Map<String, Object> getParams() {
        return params != null ? params : Collections.<String,Object>emptyMap();
    }

    @Override
    public String toString() {
        return "RemoteMethodAction{" +
               "methodExpression='" + methodExpression + '\'' +
               ", resultPath='" + resultPath + '\'' +
               ", autoRefreshActionContext=" + autoRefreshActionContext +
               ", completeAction=" + completeAction +
               ", errorAction=" + errorAction +
               ", params=" + params +
               "}";
    }
}
