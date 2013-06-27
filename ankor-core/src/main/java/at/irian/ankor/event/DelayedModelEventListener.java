package at.irian.ankor.event;

/**
 * @author Manfred Geiler
 */
public interface DelayedModelEventListener<E> {

    void processImmediately(E event);

}
