package at.irian.ankor.delay;

/**
 * @author Manfred Geiler
 */
public interface Scheduler {

    void init();

    Cancellable schedule(long delayMillis, Runnable runnable);

    void close();
}
