package at.irian.ankor.action;

import at.irian.ankor.event.ModelEvent;
import at.irian.ankor.event.ModelEventListener;
import at.irian.ankor.event.PropertyWatchModelEventListener;
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
        return listener instanceof Listener;
    }

    @Override
    public void processBy(ModelEventListener listener) {
        ((Listener) listener).process(this);
    }

    public abstract static class Listener extends PropertyWatchModelEventListener {

        protected Listener(Ref watchedProperty) {
            super(watchedProperty);
        }

        public abstract void process(ActionEvent event);
    }
}
