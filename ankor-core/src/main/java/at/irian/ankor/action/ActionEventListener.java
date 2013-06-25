package at.irian.ankor.action;

import at.irian.ankor.event.ModelEventListener;
import at.irian.ankor.ref.Ref;

/**
* @author MGeiler (Manfred Geiler)
*/
public abstract class ActionEventListener implements ModelEventListener {

    private final Ref watchedProperty;

    protected ActionEventListener(Ref watchedProperty) {
        this.watchedProperty = watchedProperty;
    }

    @Override
    public boolean isDiscardable() {
        return watchedProperty != null && !watchedProperty.isValid();
    }

    public Ref getWatchedProperty() {
        return watchedProperty;
    }

    public abstract void processAction(Ref actionProperty, Action action);
}
