package at.irian.ankor.delay;

/**
 * @author Manfred Geiler
 */
public interface Scheduler {
    Cancellable schedule(long delayMillis, Runnable runnable);
}
