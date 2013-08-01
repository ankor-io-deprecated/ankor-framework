package at.irian.ankor.delay;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author Manfred Geiler
 */
public class SimpleScheduler implements Scheduler {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleScheduler.class);

    private final ScheduledExecutorService executorService;

    public SimpleScheduler() {
        this.executorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "Ankor scheduler");
            }
        });
    }

    @Override
    public void schedule(long delayMillis, Runnable runnable) {
        executorService.schedule(runnable, delayMillis, TimeUnit.MILLISECONDS);
    }
}
