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

    private final WorkerContext workerContext;

    public JavaFxEventDispatcher(ModelSession modelSession, WorkerContext workerContext) {
        super(modelSession);
        this.workerContext = workerContext;
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
