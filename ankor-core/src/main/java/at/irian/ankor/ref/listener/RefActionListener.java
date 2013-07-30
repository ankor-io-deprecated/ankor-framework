package at.irian.ankor.ref.listener;

import at.irian.ankor.action.Action;
import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public interface RefActionListener extends RefListener {

    void processAction(Ref sourceProperty, Action action);

}
