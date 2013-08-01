package at.irian.ankor.delay;

/**
 * @author Manfred Geiler
 */
public interface Scheduler {
    void schedule(long delayMillis, Runnable runnable);
}
