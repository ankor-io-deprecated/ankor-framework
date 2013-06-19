package at.irian.ankor.fx.app;

import at.irian.ankor.core.action.method.RemoteMethodAction;
import at.irian.ankor.core.action.ModelAction;
import at.irian.ankor.core.action.SimpleAction;
import at.irian.ankor.core.application.Application;
import at.irian.ankor.core.listener.ActionListener;
import at.irian.ankor.core.ref.Ref;

/**
 * @author Thomas Spiegl
 */
public class AppService {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AppService.class);

    private final Application application;

    private ActionCompleteCallback currentCallback = null;

    public AppService(Application application) {
        application.getListenerRegistry().registerRemoteActionListener(null,
                new ActionListener() {
                    public void processAction(Ref actionContext, ModelAction action) {
                        if (currentCallback != null && action.name().equals("cb")) {
                            currentCallback.onComplete();
                        }
                    }
                });
        this.application = application;
    }

    public Application getApplication() {
        return application;
    }

    public synchronized void executeAction(Ref contextRef, String actionMethod, String resultPath, ActionCompleteCallback cb) {
        ModelAction completeAction = SimpleAction.create("cb");
        currentCallback = cb;
        contextRef.fire(RemoteMethodAction
                .create(actionMethod)
                .withResultIn(resultPath)
                .onComplete(completeAction));
    }
}
