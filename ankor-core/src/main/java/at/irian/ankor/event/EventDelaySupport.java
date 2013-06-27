package at.irian.ankor.event;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Manfred Geiler
 */
public class EventDelaySupport {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(EventDelaySupport.class);

    private final ScheduledExecutorService executorService;

    public EventDelaySupport() {
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public EventDelay createEventDelayFor(DelayedModelEventListener listener, long delayMilliseconds) {
        return new UnsynchronizedEventDelay(executorService, listener, delayMilliseconds);
    }



    public static final EventDelaySupport INSTANCE = new EventDelaySupport();

    public static EventDelaySupport getInstance() {
        return INSTANCE;
    }
}
