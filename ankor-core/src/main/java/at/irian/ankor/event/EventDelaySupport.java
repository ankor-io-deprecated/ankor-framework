package at.irian.ankor.event;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * @author Manfred Geiler
 */
@Deprecated
public class EventDelaySupport {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(EventDelaySupport.class);

    private final ScheduledExecutorService executorService;

    public EventDelaySupport(final String systemName) {
        this.executorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "Ankor '" + systemName + "' - event delayer");
            }
        });
    }

    public EventDelay createEventDelayFor(DelayedModelEventListener listener, long delayMilliseconds) {
        return new UnsynchronizedEventDelay(executorService, listener, delayMilliseconds);
    }

}
