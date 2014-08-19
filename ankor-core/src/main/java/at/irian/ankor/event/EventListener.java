package at.irian.ankor.event;

/**
 * Base type for all Ankor event listeners.
 *
 * @author Manfred Geiler
 */
public interface EventListener {

    /**
     * @return true, if this listener is no longer needed and is candidate to listener garbage collection
     */
    boolean isDiscardable();

}
