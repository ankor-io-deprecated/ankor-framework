package at.irian.ankor.event.dispatch;

import at.irian.ankor.session.ModelSession;
import at.irian.ankor.worker.WorkerContext;

/**
 * @author Manfred Geiler
 */
public class JavaFxEventDispatcherFactory implements EventDispatcherFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SynchronisedEventDispatcherFactory.class);

    private final WorkerContext workerContext;

    public JavaFxEventDispatcherFactory(WorkerContext workerContext) {
        this.workerContext = workerContext;
    }

    @Deprecated
    public JavaFxEventDispatcherFactory() {
        this.workerContext = new WorkerContext();
    }

    @Override
    public EventDispatcher createFor(ModelSession modelSession) {
        return new JavaFxEventDispatcher(modelSession, workerContext);
    }

}
