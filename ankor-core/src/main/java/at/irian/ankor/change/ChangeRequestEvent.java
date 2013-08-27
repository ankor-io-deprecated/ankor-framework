package at.irian.ankor.change;

import at.irian.ankor.event.ModelEvent;
import at.irian.ankor.event.ModelEventListener;
import at.irian.ankor.ref.Ref;

/**
 * This ModelEvent signals a change that someone outside the managed model context system has requested.
 * Typical use case for a ChangeRequestEvent is a user interface event handler that handles
 * user interface events (e.g. mouse click or keyboard event). If this handler wants to change
 * the model (e.g. for two-way-binding) he must not directly change the model because he has no exclusive
 * (actor managed or synchronized) access to the model. A direct model access would cause nasty
 * concurrency issues.
 * By firing a ChangeRequestEvent instead, the ankor system will do the requested model change in a
 * controlled way.
 *
 * @author Manfred Geiler
 */
public class ChangeRequestEvent extends ModelEvent {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ChangeRequestEvent.class);

    private final Change change;

    public ChangeRequestEvent(Ref propertyToChange, Change change) {
        super(propertyToChange);
        this.change = change;
    }

    public Ref getPropertyToChange() {
        return getSourceProperty();
    }

    public Change getChange() {
        return change;
    }

    @Override
    public boolean isAppropriateListener(ModelEventListener listener) {
        return listener instanceof ChangeRequestEventListener;
    }

    @Override
    public void processBy(ModelEventListener listener) {
        ((ChangeRequestEventListener)listener).processChangeRequest(this);
    }

}
