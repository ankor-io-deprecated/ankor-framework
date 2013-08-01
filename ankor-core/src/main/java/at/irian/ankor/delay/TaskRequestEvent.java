package at.irian.ankor.delay;

import at.irian.ankor.event.ModelEvent;
import at.irian.ankor.event.ModelEventListener;
import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public class TaskRequestEvent extends ModelEvent {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TaskRequestEvent.class);

    private final Runnable task;

    public TaskRequestEvent(Ref source, Runnable task) {
        super(source);
        this.task = task;
    }

    public Runnable getTask() {
        return task;
    }

    @Override
    public boolean isAppropriateListener(ModelEventListener listener) {
        return listener instanceof TaskRequestEventListener;
    }

    @Override
    public void processBy(ModelEventListener listener) {
        ((TaskRequestEventListener)listener).processTaskRequest(this);
    }
}
