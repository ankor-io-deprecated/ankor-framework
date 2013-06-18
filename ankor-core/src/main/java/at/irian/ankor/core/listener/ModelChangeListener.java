package at.irian.ankor.core.listener;

import at.irian.ankor.core.ref.ModelRef;

/**
 * @author MGeiler (Manfred Geiler)
 */
public interface ModelChangeListener {
    void handleModelChange(ModelRef watchedRef, ModelRef changedRef);
}
