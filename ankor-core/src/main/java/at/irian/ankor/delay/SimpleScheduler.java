package at.irian.ankor.delay;

import java.util.concurrent.*;

/**
 * @author Manfred Geiler
 */
public class SimpleScheduler implements Scheduler {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleScheduler.class);

    private ScheduledExecutorService executorService = null;

    @Override
    public void init() {
        this.executorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "Ankor scheduler");
            }
        });
    }

    @Override
    public Cancellable schedule(long delayMillis, Runnable runnable) {
        final ScheduledFuture<?> future = executorService.schedule(runnable, delayMillis, TimeUnit.MILLISECONDS);
        return new Cancellable() {
            @Override
            public void cancel() {
                future.cancel(false);
            }
        };
    }

    @Override
    public void close() {
        executorService.shutdown();
    }
}
