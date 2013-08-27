package at.irian.ankor.change;

import at.irian.ankor.event.ModelEventListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.impl.RefImplementor;

/**
 * @author Manfred Geiler
 */
public class ChangeRequestEventListener implements ModelEventListener {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ChangeRequestEventListener.class);

    public void processChangeRequest(ChangeRequestEvent changeRequestEvent) {
        Ref propertyToChange = changeRequestEvent.getPropertyToChange();
        Change change = changeRequestEvent.getChange();
        ((RefImplementor)propertyToChange).apply(change);
    }

    @Override
    public boolean isDiscardable() {
        return false;
    }

}
