package at.irian.ankor.action;

import at.irian.ankor.event.ModelEventListener;
import at.irian.ankor.event.ModelPropertyEvent;
import at.irian.ankor.event.source.Source;
import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public class ActionEvent extends ModelPropertyEvent {

    private final Action action;

    public ActionEvent(Source source, Ref actionProperty, Action action) {
        super(source, actionProperty);
        this.action = action;
    }

    public Ref getActionProperty() {
        return getProperty();
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
