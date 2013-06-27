package at.irian.ankor.fx.app;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.action.SimpleAction;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.rmi.RemoteMethodAction;
import at.irian.ankor.system.AnkorSystem;

/**
 * @author Thomas Spiegl
 */
public class AppService {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AppService.class);

    private ActionCompleteCallback currentCallback = null;
    private RefFactory refFactory;

    public AppService(AnkorSystem system) {

        refFactory = system.getRefContextFactory().create().refFactory();

        system.getGlobalEventListeners().add(new ActionEvent.Listener(null) {

            @Override
            public void process(ActionEvent event) {
                Action action = event.getAction();
                if (currentCallback != null
                    && action instanceof SimpleAction
                    && ((SimpleAction) action).getName().equals("cb")) {
                    currentCallback.onComplete();
                }
            }
        });
    }

    @Deprecated
    public synchronized void executeAction(Ref contextRef, String actionMethod, String resultPath, ActionCompleteCallback cb) {
        Action completeAction = new SimpleAction("cb");
        currentCallback = cb;
        contextRef.fireAction(RemoteMethodAction
                                      .create(actionMethod)
                                      .withResultIn(resultPath)
                                      .onComplete(completeAction));
    }


    public RMAExecution remoteMethod(String method) {
        return new RMAExecution(method);
    }

    public RefFactory getRefFactory() {
        return refFactory;
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

        public RMAExecution withActionContextAutoRefresh() {
            rma = rma.withActionContextAutoRefresh();
            return this;
        }

        public RMAExecution onComplete(ActionCompleteCallback cb) {
            this.cb = cb;
            Action completeAction = new SimpleAction("cb");
            rma = rma.onComplete(completeAction);
            return this;
        }

        public void execute() {
            fireRMAExecution(this);
        }
    }

    private synchronized void fireRMAExecution(RMAExecution rmaExecution) {
        currentCallback = rmaExecution.cb;
        rmaExecution.contextRef.fireAction(rmaExecution.rma);
    }

}
