package at.irian.ankor.ref;

/**
 * @author MGeiler (Manfred Geiler)
 */
public interface ChangeListener {

    void processChange(Ref watchedProperty, Ref changedProperty);

}
