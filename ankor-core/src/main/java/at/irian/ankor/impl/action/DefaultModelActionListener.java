package at.irian.ankor.impl.action;

import at.irian.ankor.api.action.ModelAction;
import at.irian.ankor.api.action.ModelActionListener;
import at.irian.ankor.api.context.ServerContext;

import javax.el.ELContext;
import javax.el.MethodExpression;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class DefaultModelActionListener implements ModelActionListener {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultModelActionListener.class);

    @Override
    public void handleAction(ServerContext context, ModelAction action) {
        ELContext elContext = context.getELContext();
        MethodExpression methodCall = action.getMethodCall();
        methodCall.invoke(elContext, null);
    }

}
