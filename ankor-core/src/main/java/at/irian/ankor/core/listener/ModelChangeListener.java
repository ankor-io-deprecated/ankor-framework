package at.irian.ankor.core.listener;

import at.irian.ankor.core.ref.Ref;

/**
 * @author MGeiler (Manfred Geiler)
 */
public interface ModelChangeListener {
    void handleModelChange(Ref contextRef, Ref watchedRef, Ref changedRef);
}
