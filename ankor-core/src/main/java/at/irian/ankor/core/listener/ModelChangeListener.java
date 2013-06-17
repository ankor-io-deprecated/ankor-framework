package at.irian.ankor.core.listener;

import at.irian.ankor.core.ref.ModelRef;

/**
 * @author MGeiler (Manfred Geiler)
 */
public interface ModelChangeListener {
    void beforeModelChange(ModelRef modelRef, Object oldValue, Object newValue);
    void afterModelChange(ModelRef modelRef, Object oldValue, Object newValue);
}
