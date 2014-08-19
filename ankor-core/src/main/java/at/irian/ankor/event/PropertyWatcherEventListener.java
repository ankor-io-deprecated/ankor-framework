package at.irian.ankor.event;

import at.irian.ankor.ref.Ref;

/**
 * todo: abandon this base class and put functionality directly into derived classes
 *
 * @author Manfred Geiler
 */
public abstract class PropertyWatcherEventListener implements EventListener {

    private final Ref watchedProperty;

    protected PropertyWatcherEventListener(Ref watchedProperty) {
        this.watchedProperty = watchedProperty;
    }

    public Ref getWatchedProperty() {
        return watchedProperty;
    }

    @Override
    public boolean isDiscardable() {
        return watchedProperty != null && !watchedProperty.isValid();
    }

}
