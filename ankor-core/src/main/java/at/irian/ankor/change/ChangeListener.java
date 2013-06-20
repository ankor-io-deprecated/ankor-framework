package at.irian.ankor.change;

import at.irian.ankor.ref.Ref;

/**
 * @author MGeiler (Manfred Geiler)
 */
public interface ChangeListener {
    void processChange(Ref contextRef, Ref watchedRef, Ref changedRef);
}
