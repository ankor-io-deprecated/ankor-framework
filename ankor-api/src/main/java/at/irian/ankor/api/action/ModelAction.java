package at.irian.ankor.api.action;

import javax.el.MethodExpression;

/**
 */
public class ModelAction {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelAction.class);

    private final MethodExpression methodCall;

    public ModelAction(MethodExpression methodCall) {
        this.methodCall = methodCall;
    }

    public MethodExpression getMethodCall() {
        return methodCall;
    }
}
