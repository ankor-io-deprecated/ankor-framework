package at.irian.ankor.core.listener;

import at.irian.ankor.core.action.ModelAction;
import at.irian.ankor.core.ref.Ref;

/**
 * @author MGeiler (Manfred Geiler)
 */
public interface ModelActionListener {
    void handleModelAction(Ref actionContext, ModelAction action);
}
