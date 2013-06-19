package at.irian.ankor.core.action.method;

import at.irian.ankor.core.action.CompleteAware;
import at.irian.ankor.core.action.ModelAction;
import at.irian.ankor.core.ref.Ref;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class RemoteMethodAction implements ModelAction, CompleteAware {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RemoteMethodAction.class);

    private final String methodExpression;
    private final String resultPath;
    private final ModelAction completeAction;

    public RemoteMethodAction(String methodExpression) {
        this(methodExpression, null, null);
    }

    public RemoteMethodAction(String methodExpression, String resultPath) {
        this(methodExpression, resultPath, null);
    }

    public RemoteMethodAction(String methodExpression, ModelAction completeAction) {
        this(methodExpression, null, completeAction);
    }

    public RemoteMethodAction(String methodExpression, String resultPath, ModelAction completeAction) {
        this.methodExpression = methodExpression;
        this.resultPath = resultPath;
        this.completeAction = completeAction;
    }

    public String getMethodExpression() {
        return methodExpression;
    }

    @Override
    public String name() {
        return methodExpression;
    }

    public Ref getResultRef(Ref actionContext) {
        return resultPath != null ? actionContext.sub(resultPath) : null;
    }

    @Override
    public void complete(Ref actionContext) {
        if (completeAction != null) {
            actionContext.fire(completeAction);
        }
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
