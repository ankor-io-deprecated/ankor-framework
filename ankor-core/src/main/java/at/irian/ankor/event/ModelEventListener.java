package at.irian.ankor.event;

/**
 * @author Manfred Geiler
 */
public interface ModelEventListener {

    /**
     * @return true, if this listener is no longer needed and is candidate to listener garbage collection
     */
    boolean isDiscardable();

}
