package at.irian.ankor.core.listener;

import at.irian.ankor.core.ref.ModelRef;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ModelChangeListenerInstance {
    private final ModelRef ref;
    private final ModelChangeListener listener;

    public ModelChangeListenerInstance(ModelRef ref, ModelChangeListener listener) {
        this.ref = ref;
        this.listener = listener;
    }

    public ModelRef getRef() {
        return ref;
    }

    public ModelChangeListener getListener() {
        return listener;
    }
}
