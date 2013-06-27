package at.irian.ankor.change;

import at.irian.ankor.event.ModelEvent;
import at.irian.ankor.event.ModelEventListener;
import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public class ChangeEvent extends ModelEvent {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ChangeEvent.class);

    public ChangeEvent(Ref changedProperty) {
        super(changedProperty);
    }

    public Ref getChangedProperty() {
        return (Ref)source;
    }

    @Override
    public boolean isAppropriateListener(ModelEventListener listener) {
        return listener instanceof ChangeEventListener;
    }

    @Override
    public void processBy(ModelEventListener listener) {
        if (listener instanceof ChangeEventListener) {
            ((ChangeEventListener)listener).process(this);
        }
    }

}
