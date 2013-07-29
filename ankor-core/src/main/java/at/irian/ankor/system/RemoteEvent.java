package at.irian.ankor.system;

import at.irian.ankor.event.ModelEvent;
import at.irian.ankor.event.ModelEventListener;
import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public class RemoteEvent extends ModelEvent {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RemoteEvent.class);

    private final RemoteChange change;
    private final RemoteAction action;

    public RemoteEvent(Ref changedProperty, RemoteChange change) {
        super(changedProperty);
        this.change = change;
        this.action = null;
    }

    public RemoteEvent(Ref source, RemoteAction action) {
        super(source);
        this.change = null;
        this.action = action;
    }

    public RemoteChange getChange() {
        return change;
    }

    public RemoteAction getAction() {
        return action;
    }

    @Override
    public boolean isAppropriateListener(ModelEventListener listener) {
        return listener instanceof RemoteEventListener;
    }

    @Override
    public void processBy(ModelEventListener listener) {
        ((RemoteEventListener)listener).process(this);
    }
}
