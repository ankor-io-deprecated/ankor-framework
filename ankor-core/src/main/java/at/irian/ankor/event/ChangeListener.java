package at.irian.ankor.event;

import at.irian.ankor.ref.Ref;

/**
 * @author MGeiler (Manfred Geiler)
 */
public interface ChangeListener {
    void processChange(Ref modelContext, Ref watchedProperty, Ref changedProperty);
}
