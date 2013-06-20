package at.irian.ankor.event;

import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public interface ChangeNotifier {
    void broadcastChange(Ref modelContext, Ref changedProperty);
}
