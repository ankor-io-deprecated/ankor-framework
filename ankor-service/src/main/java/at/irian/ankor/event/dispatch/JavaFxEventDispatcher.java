package at.irian.ankor.event.dispatch;

import at.irian.ankor.event.ModelEvent;
import at.irian.ankor.worker.WorkerContext;
import javafx.application.Platform;

/**
 * @author Manfred Geiler
 */
public class JavaFxEventDispatcher implements EventDispatcher {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(JavaFxEventDispatcher.class);

    private final EventDispatcher delegateEventDispatcher;
    private final WorkerContext workerContext;

    public JavaFxEventDispatcher(EventDispatcher delegateEventDispatcher) {
        this.delegateEventDispatcher = delegateEventDispatcher;
        this.workerContext = new WorkerContext();
    }

    @Override
    public void dispatch(final ModelEvent event) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                WorkerContext.setCurrentInstance(workerContext);
                try {
                    delegateEventDispatcher.dispatch(event);
                } finally {
                    WorkerContext.setCurrentInstance(null);
                }
            }
        });
    }

    @Override
    public void close() {
        delegateEventDispatcher.close();
    }
}
