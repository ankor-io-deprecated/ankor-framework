package at.irian.ankor.core.listener;

import at.irian.ankor.core.ref.Ref;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class BoundChangeListener {
    private final Ref ref;
    private final ChangeListener listener;

    public BoundChangeListener(Ref ref, ChangeListener listener) {
        this.ref = ref;
        this.listener = listener;
    }

    public Ref getRef() {
        return ref;
    }

    public ChangeListener getListener() {
        return listener;
    }
}
