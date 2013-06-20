package at.irian.ankor.application;

import at.irian.ankor.event.ChangeListener;
import at.irian.ankor.ref.Ref;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class BoundChangeListener {
    private final Ref watchedRef;
    private final ChangeListener listener;

    public BoundChangeListener(Ref watchedRef, ChangeListener listener) {
        this.watchedRef = watchedRef;
        this.listener = listener;
    }

    public Ref getWatchedRef() {
        return watchedRef;
    }

    public ChangeListener getListener() {
        return listener;
    }
}
