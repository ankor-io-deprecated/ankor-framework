package at.irian.ankor.delay;

import at.irian.ankor.event.Event;
import at.irian.ankor.event.EventListener;
import at.irian.ankor.event.source.Source;

/**
 * @author Manfred Geiler
 */
public class TaskRequestEvent extends Event {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TaskRequestEvent.class);

    private final Runnable task;

    public TaskRequestEvent(Source source, Runnable task) {
        super(source);
        this.task = task;
    }

    public Runnable getTask() {
        return task;
    }

    @Override
    public boolean isAppropriateListener(EventListener listener) {
        return listener instanceof TaskRequestEventListener;
    }

    @Override
    public void processBy(EventListener listener) {
        ((TaskRequestEventListener)listener).processTaskRequest(this);
    }

    @Override
    public String toString() {
        return "TaskRequestEvent{" +
               "source=" + getSource() +
               ", task=" + task +
               "} " + super.toString();
    }
}
