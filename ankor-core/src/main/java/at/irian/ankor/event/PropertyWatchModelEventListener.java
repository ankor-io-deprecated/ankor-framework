package at.irian.ankor.event;

import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public abstract class PropertyWatchModelEventListener implements ModelEventListener {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PropertyWatchModelEventListener.class);

    private final Ref watchedProperty;

    protected PropertyWatchModelEventListener(Ref watchedProperty) {
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
