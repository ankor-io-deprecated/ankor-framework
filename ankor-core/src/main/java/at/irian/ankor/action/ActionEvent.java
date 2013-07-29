package at.irian.ankor.action;

import at.irian.ankor.event.ModelEvent;
import at.irian.ankor.event.ModelEventListener;
import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
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
        return listener instanceof ActionEventListener;
    }

    @Override
    public void processBy(ModelEventListener listener) {
        ((ActionEventListener) listener).process(this);
    }

    @Override
    public String toString() {
        return "ActionEvent{" +
               "actionProperty=" + getActionProperty() +
               ", action=" + action +
               '}';
    }
}
