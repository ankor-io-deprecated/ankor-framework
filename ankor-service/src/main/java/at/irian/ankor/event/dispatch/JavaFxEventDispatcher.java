package at.irian.ankor.event.dispatch;

import at.irian.ankor.session.ModelSession;
import at.irian.ankor.event.ModelEvent;
import at.irian.ankor.worker.WorkerContext;
import javafx.application.Platform;

/**
 * @author Manfred Geiler
 */
public class JavaFxEventDispatcher extends SynchronisedEventDispatcher {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(JavaFxEventDispatcher.class);

    private WorkerContext workerContext = new WorkerContext();

    public JavaFxEventDispatcher(ModelSession modelSession) {
        super(modelSession);
    }

    @Override
    public void dispatch(final ModelEvent event) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    WorkerContext.setCurrentInstance(workerContext);
                    JavaFxEventDispatcher.super.dispatch(event);
                } finally {
                    WorkerContext.setCurrentInstance(null);
                }
            }
        });
    }
}
