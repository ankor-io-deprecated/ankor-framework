package at.irian.ankor.event;

import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public abstract class PropertyWatcherEventListener implements EventListener, PropertyWatcher, RefOwned {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PropertyWatcherEventListener.class);

    private final Ref watchedProperty;

    protected PropertyWatcherEventListener(Ref watchedProperty) {
        this.watchedProperty = watchedProperty;
    }

    @Override
    public Ref getWatchedProperty() {
        return watchedProperty;
    }

    @Override
    public Ref getOwner() {
        return watchedProperty;
    }

    @Override
    public boolean isDiscardable() {
        Ref owner = getOwner();
        return owner != null && !owner.isValid();
    }

}
