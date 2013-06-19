package at.irian.ankor.core.listener;

import at.irian.ankor.core.action.ModelAction;
import at.irian.ankor.core.ref.Ref;

/**
 * @author MGeiler (Manfred Geiler)
 */
//todo rename
public interface ModelActionListener {
    //todo rename to processAction
    void handleModelAction(Ref actionContextRef, ModelAction action);
}
