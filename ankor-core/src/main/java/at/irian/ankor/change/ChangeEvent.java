package at.irian.ankor.change;

import at.irian.ankor.event.ModelEvent;
import at.irian.ankor.event.ModelEventListener;
import at.irian.ankor.event.PropertyWatchModelEventListener;
import at.irian.ankor.ref.Ref;

/**
 * @author MGeiler (Manfred Geiler)
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
        return listener instanceof Listener || listener instanceof TreeListener;
    }

    @Override
    public void processBy(ModelEventListener listener) {
        if (listener instanceof Listener) {
            ((Listener)listener).process(this);
        } else {
            ((TreeListener)listener).process(this);
        }
    }

    public abstract static class Listener extends PropertyWatchModelEventListener {

        protected Listener(Ref watchedProperty) {
            super(watchedProperty);
        }

        public abstract void process(ChangeEvent event);
    }

    public abstract static class TreeListener extends PropertyWatchModelEventListener {

        protected TreeListener(Ref watchedProperty) {
            super(watchedProperty);
        }

        public abstract void process(ChangeEvent event);
    }
}
