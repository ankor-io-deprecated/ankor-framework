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

    @Deprecated
    public synchronized void executeAction(Ref contextRef, String actionMethod, String resultPath, ActionCompleteCallback cb) {
        Action completeAction = SimpleAction.create("cb");
        currentCallback = cb;
        contextRef.fire(RemoteMethodAction
                .create(actionMethod)
                .withResultIn(resultPath)
                .onComplete(completeAction));
    }


    public RMAExecution remoteMethod(String method) {
        return new RMAExecution(method);
    }

    public class RMAExecution {
        private RemoteMethodAction rma;
        private Ref contextRef;
        private ActionCompleteCallback cb;

        public RMAExecution(String method) {
            this.rma = RemoteMethodAction.create(method);
        }

        public RMAExecution inContext(Ref contextRef) {
            this.contextRef = contextRef;
            return this;
        }

        public RMAExecution withResultIn(Ref resultRef) {
            rma = rma.withResultIn(resultRef.path());
            return this;
        }

        public RMAExecution withResultIn(String resultPath) {
            rma = rma.withResultIn(resultPath);
            return this;
        }

        public RMAExecution setParam(String name, Object value) {
            rma = rma.setParam(name, value);
            return this;
        }

        public RMAExecution onComplete(ActionCompleteCallback cb) {
            this.cb = cb;
            Action completeAction = SimpleAction.create("cb");
            rma = rma.onComplete(completeAction);
            return this;
        }

        public void execute() {
            fireRMAExecution(this);
        }
    }

    private synchronized void fireRMAExecution(RMAExecution rmaExecution) {
        currentCallback = rmaExecution.cb;
        rmaExecution.contextRef.fire(rmaExecution.rma);
    }



}
