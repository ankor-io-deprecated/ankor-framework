package at.irian.ankor.delay;

import at.irian.ankor.event.ModelEventListener;

/**
 * @author Manfred Geiler
 */
public class TaskRequestEventListener implements ModelEventListener {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TaskRequestEventListener.class);

    public void processTaskRequest(TaskRequestEvent taskRequestEvent) {
        taskRequestEvent.getTask().run();
    }

    @Override
    public boolean isDiscardable() {
        return false;
    }
}
