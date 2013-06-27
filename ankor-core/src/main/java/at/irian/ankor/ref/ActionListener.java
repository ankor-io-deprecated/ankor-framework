package at.irian.ankor.ref;

import at.irian.ankor.action.Action;

/**
 * @author Manfred Geiler
 */
public interface ActionListener {

    void processAction(Ref sourceProperty, Action action);

}
