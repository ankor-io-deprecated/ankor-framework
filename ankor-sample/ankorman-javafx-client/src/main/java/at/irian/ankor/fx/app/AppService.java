package at.irian.ankor.fx.app;

import at.irian.ankor.action.Action;
import at.irian.ankor.service.rma.RemoteMethodAction;
import at.irian.ankor.action.SimpleAction;
import at.irian.ankor.application.Application;
import at.irian.ankor.event.ActionListener;
import at.irian.ankor.ref.Ref;

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
                    public void processAction(Ref modelContext, Action action) {
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
        Action completeAction = SimpleAction.create("cb");
        currentCallback = cb;
        contextRef.fire(RemoteMethodAction
                .create(actionMethod)
                .withResultIn(resultPath)
                .onComplete(completeAction));
    }
}
