package at.irian.ankor.change;

import at.irian.ankor.event.ModelEventListener;
import at.irian.ankor.ref.Ref;

/**
* @author MGeiler (Manfred Geiler)
*/
public abstract class ChangeEventListener implements ModelEventListener {

    private final Ref watchedProperty;

    protected ChangeEventListener(Ref watchedProperty) {
        this.watchedProperty = watchedProperty;
    }

    @Override
    public boolean isDiscardable() {
        return watchedProperty != null && !watchedProperty.isValid();
    }

    public Ref getWatchedProperty() {
        return watchedProperty;
    }

    public abstract void processChange(Ref changedProperty);
}
