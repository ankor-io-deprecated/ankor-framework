package at.irian.ankor.event;

/**
 * @author Manfred Geiler
 */
public interface EventListeners extends Iterable<ModelEventListener> {

    void add(ModelEventListener listener);

    void remove(ModelEventListener listener);

    /**
     * Removes all discardable listeners.
     */
    void cleanup();

}
