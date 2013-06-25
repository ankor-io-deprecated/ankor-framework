package at.irian.ankor.ref;

import at.irian.ankor.action.Action;

/**
 * @author MGeiler (Manfred Geiler)
 */
public interface ActionListener {

    void processAction(Ref sourceProperty, Action action);

}
