package at.irian.ankor.ref;

/**
 * @author Manfred Geiler
 */
public interface ChangeListener {

    void processChange(Ref watchedProperty, Ref changedProperty);

}
