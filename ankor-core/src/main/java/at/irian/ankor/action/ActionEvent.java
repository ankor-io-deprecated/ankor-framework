package at.irian.ankor.action;

import at.irian.ankor.event.EventListener;
import at.irian.ankor.event.ModelPropertyEvent;
import at.irian.ankor.event.source.Source;
import at.irian.ankor.ref.Ref;

/**
 * A {@link at.irian.ankor.event.ModelPropertyEvent} that carries an {@link Action} as payload.
 *
 * @author Manfred Geiler
 */
public class ActionEvent extends ModelPropertyEvent {

    private final Action action;

    public ActionEvent(Source source, Ref actionProperty, Action action) {
        super(source, actionProperty);
        this.action = action;
    }

    /**
     * @return the {@link Action} that is associated with this event
     */
    public Action getAction() {
        return action;
    }

    /**
     * @param listener  an {@link EventListener} instance
     * @return true, if the given listener is an {@link ActionEventListener}
     */
    @Override
    public boolean isAppropriateListener(EventListener listener) {
        return listener instanceof ActionEventListener;
    }

    /**
     * Casts the given listener to {@link ActionEventListener} and
     * dispatches to its {@link at.irian.ankor.action.ActionEventListener#process(ActionEvent)} method
     * @param listener  an {@link EventListener} instance
     */
    @Override
    public void processBy(EventListener listener) {
        ((ActionEventListener) listener).process(this);
    }

    @Override
    public String toString() {
        return "ActionEvent{" +
               "property=" + getProperty() +
               ", action=" + action +
               '}';
    }
}
