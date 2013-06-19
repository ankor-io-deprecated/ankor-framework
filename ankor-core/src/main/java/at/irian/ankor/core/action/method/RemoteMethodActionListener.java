package at.irian.ankor.core.action.method;

import at.irian.ankor.core.action.CompleteAware;
import at.irian.ankor.core.action.ModelAction;
import at.irian.ankor.core.application.Application;
import at.irian.ankor.core.listener.ModelActionListener;
import at.irian.ankor.core.ref.Ref;

/**
* @author MGeiler (Manfred Geiler)
*/
public class RemoteMethodActionListener implements ModelActionListener {

    private final Application application;
    private final MethodExecutor methodExecutor;

    public RemoteMethodActionListener(Application application) {
        this.application = application;
        this.methodExecutor = new MethodExecutor(application.getELSupport().getExpressionFactory(),
                                                 application.getELSupport().getStandardELContext(),
                                                 application.getModelHolder(),
                                                 application.getBeanResolver());
    }

    @Override
    public void handleModelAction(Ref actionContext, ModelAction action) {
        if (action instanceof RemoteMethodAction) {
            RemoteMethodAction remoteMethodAction = (RemoteMethodAction) action;
            String methodExpression = remoteMethodAction.getMethodExpression();
            Object result = methodExecutor.execute(methodExpression, actionContext);
            Ref resultRef = remoteMethodAction.getResultRef(actionContext);
            if (resultRef != null) {
                resultRef.setValue(result);
            } else {
                application.getModelChangeNotifier().notifyLocalListeners(actionContext);
            }
            remoteMethodAction.complete(actionContext);
        }
    }
}
