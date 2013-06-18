package at.irian.ankor.core.action;

import at.irian.ankor.core.ref.ModelRef;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class MethodAction implements ModelAction, CompleteAware {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MethodAction.class);

    private final String methodExpression;
    private final ModelAction completeAction;

    public MethodAction(String methodExpression, ModelAction completeAction) {
        this.methodExpression = methodExpression;
        this.completeAction = completeAction;
    }

    public String getMethodExpression() {
        return methodExpression;
    }

    @Override
    public String name() {
        return methodExpression;
    }

    @Override
    public void complete(ModelRef actionContext) {
        if (completeAction != null) {
            actionContext.fire(completeAction);
        }
    }

    @Override
    public String toString() {
        return "MethodAction{" +
               "methodExpression='" + methodExpression + '\'' +
               ", completeAction=" + completeAction +
               '}';
    }
}
