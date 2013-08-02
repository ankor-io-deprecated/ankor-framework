package at.irian.ankor.delay;

import at.irian.ankor.event.dispatch.EventDispatcher;
import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public class FloodControl {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DelaySupport.class);

    private final Ref ref;
    private final Scheduler scheduler;
    private final EventDispatcher eventDispatcher;
    private final long delay;
    private volatile Cancellable lastDelayed;

    public FloodControl(Ref ref, long delay) {
        this.ref = ref;
        this.scheduler = ref.context().scheduler();
        this.eventDispatcher = ref.context().modelContext().getEventDispatcher();
        this.delay = delay;
    }

    public void control(final Runnable task) {
        if (lastDelayed != null) {
            lastDelayed.cancel();
        }
        lastDelayed = scheduler.schedule(delay, new Runnable() {
            @Override
            public void run() {
                eventDispatcher.dispatch(new TaskRequestEvent(ref, task));
            }
        });
    }

}
