package at.irian.ankor.event;

import at.irian.ankor.action.Action;
import at.irian.ankor.ref.Ref;

/**
 * @author MGeiler (Manfred Geiler)
 */
public interface ActionListener {
    void processAction(Ref modelContext, Action action);
}
