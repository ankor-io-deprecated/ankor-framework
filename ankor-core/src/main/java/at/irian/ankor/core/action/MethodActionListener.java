package at.irian.ankor.core.action;

import at.irian.ankor.core.application.Application;
import at.irian.ankor.core.listener.ModelActionListener;
import at.irian.ankor.core.ref.Ref;

/**
* @author MGeiler (Manfred Geiler)
*/
public class MethodActionListener implements ModelActionListener {

    private final Application application;

    public MethodActionListener(Application application) {
        this.application = application;
    }

    @Override
    public void handleModelAction(Ref actionContext, ModelAction action) {
        if (action instanceof MethodAction) {
            MethodAction methodAction = (MethodAction) action;
            String methodExpression = methodAction.getMethodExpression();
            Object result = application.getMethodExecutor().execute(methodExpression, actionContext);
            Ref resultRef = methodAction.getResultRef(actionContext);
            if (resultRef != null) {
                resultRef.setValue(result);
            } else {
                application.getModelChangeWatcher().broadcastModelChange(actionContext);
            }
        }
    }
}
