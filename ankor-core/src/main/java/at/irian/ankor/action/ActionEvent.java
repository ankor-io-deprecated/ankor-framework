package at.irian.ankor.action;

import at.irian.ankor.event.ModelEvent;
import at.irian.ankor.event.ModelEventListener;
import at.irian.ankor.ref.Ref;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ActionEvent extends ModelEvent {

    private final Action action;

    public ActionEvent(Ref actionProperty, Action action) {
        super(actionProperty);
        this.action = action;
    }

    public Ref getActionProperty() {
        return getSourceProperty();
    }

    public Action getAction() {
        return action;
    }

    @Override
    public boolean isAppropriateListener(ModelEventListener listener) {
        if (listener instanceof ActionEventListener) {
            Ref watchedProperty = ((ActionEventListener) listener).getWatchedProperty();
            Ref actionProperty = getActionProperty();
            if (watchedProperty == null || watchedProperty.equals(actionProperty)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void processBy(ModelEventListener listener) {
        ((ActionEventListener)listener).processAction(getActionProperty(), getAction());
    }
}
