package at.irian.ankor.event;

import at.irian.ankor.action.Action;
import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public interface ActionNotifier {
    void broadcastAction(Ref modelContext, Action action);
}
