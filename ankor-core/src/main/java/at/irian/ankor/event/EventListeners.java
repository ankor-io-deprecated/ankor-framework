package at.irian.ankor.event;

/**
 * @author Manfred Geiler
 */
public interface EventListeners extends Iterable<EventListener> {

    void add(EventListener listener);

    void remove(EventListener listener);

    /**
     * Removes all discardable listeners.
     */
    void cleanup();

}
