package at.irian.ankor.change;

import at.irian.ankor.event.ModelEventListener;
import at.irian.ankor.event.ModelPropertyEvent;
import at.irian.ankor.event.source.Source;
import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public class ChangeEvent extends ModelPropertyEvent {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ChangeEvent.class);

    private final Change change;

    public ChangeEvent(Source source, Ref changedProperty, Change change) {
        super(source, changedProperty);
        this.change = change;
    }

    public Ref getChangedProperty() {
        return getProperty();
    }

    public Change getChange() {
        return change;
    }

    @Override
    public boolean isAppropriateListener(ModelEventListener listener) {
        return listener instanceof ChangeEventListener;
    }

    @Override
    public void processBy(ModelEventListener listener) {
        ((ChangeEventListener)listener).process(this);
    }

    @Override
    public String toString() {
        return "ChangeEvent{" +
               "changedProperty=" + getChangedProperty() +
               ", change=" + change +
               '}';
    }
}
