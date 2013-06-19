package at.irian.ankor.core.listener;

import at.irian.ankor.core.ref.Ref;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ModelChangeListenerInstance {
    private final Ref ref;
    private final ModelChangeListener listener;

    public ModelChangeListenerInstance(Ref ref, ModelChangeListener listener) {
        this.ref = ref;
        this.listener = listener;
    }

    public Ref getRef() {
        return ref;
    }

    public ModelChangeListener getListener() {
        return listener;
    }
}
