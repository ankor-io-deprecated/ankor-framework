package at.irian.ankor.core.listener;

import at.irian.ankor.core.action.ModelAction;
import at.irian.ankor.core.ref.ModelRef;

/**
 * @author MGeiler (Manfred Geiler)
 */
public interface ModelActionListener {
    void handleModelAction(ModelRef actionContext, ModelAction action);
}
