package at.irian.ankor.ref.listener;

import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public interface RefChangeListener extends RefListener {

    void processChange(Ref changedProperty);

}
