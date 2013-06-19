package at.irian.ankor.sample.fx.app;

import at.irian.ankor.core.action.MethodAction;
import at.irian.ankor.core.action.ModelAction;
import at.irian.ankor.core.action.SimpleAction;
import at.irian.ankor.core.application.Application;
import at.irian.ankor.core.listener.ModelActionListener;
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
                new ModelActionListener() {
                    public void handleModelAction(Ref actionContext, ModelAction action) {
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
        ModelAction completeAction = SimpleAction.withName("cb");
        currentCallback = cb;
        contextRef.fire(new MethodAction(actionMethod, resultPath, completeAction));
    }
}
