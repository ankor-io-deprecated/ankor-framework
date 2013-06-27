package at.irian.ankor.event;

import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public abstract class PropertyWatcherModelEventListener implements ModelEventListener, PropertyWatcher {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PropertyWatcherModelEventListener.class);

    private final Ref watchedProperty;

    protected PropertyWatcherModelEventListener(Ref watchedProperty) {
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
