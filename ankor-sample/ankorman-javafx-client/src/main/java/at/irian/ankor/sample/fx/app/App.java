package at.irian.ankor.sample.fx.app;

import at.irian.ankor.core.action.MethodAction;
import at.irian.ankor.core.action.ModelAction;
import at.irian.ankor.core.action.SimpleAction;
import at.irian.ankor.core.application.Application;
import at.irian.ankor.core.listener.ModelActionListener;
import at.irian.ankor.core.ref.ModelRef;

/**
 * @author Thomas Spiegl
 */
public class App {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(App.class);

    static Application APP_INSTANCE;

    public static void setInstance(Application clientApp) {
        APP_INSTANCE = clientApp;
        APP_INSTANCE.getListenerRegistry().registerRemoteActionListener(null,
                new ModelActionListener() {
            public void handleModelAction(ModelRef actionContext, ModelAction action) {
                if (currentCB != null && action.name().equals("cb")) {
                    currentCB.onComplete();
                }
            }
        });
    }

    public static Application getApplication() {
        return APP_INSTANCE;
    }

    private static ActionCompleteCallback currentCB = null;

    public static synchronized void executeAction(String action, ModelRef contextRef, String resultPath, final ActionCompleteCallback cb) {
        ModelAction completeAction = SimpleAction.withName("cb");
        currentCB = cb;
        contextRef.fire(new MethodAction(action, resultPath, completeAction));
    }


}
