package at.irian.ankor.system;

import at.irian.ankor.change.Change;
import at.irian.ankor.change.ChangeEvent;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.event.EventListeners;

/**
 * Global ChangeEventListener that automatically discards all ModelEventListeners that are no longer owned by valid
 * model Ref after a model property's value has changed to <code>null</code>.
 *
 * @author Manfred Geiler
 */
public class ListenerCleanupChangeEventListener extends ChangeEventListener {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ListenerCleanupChangeEventListener.class);

    private final EventListeners eventListeners;

    public ListenerCleanupChangeEventListener(EventListeners eventListeners) {
        super(null); // global listener
        this.eventListeners = eventListeners;
    }

    @Override
    public boolean isDiscardable() {
        return false; // this is a global system listener
    }

    @Override
    public void process(ChangeEvent event) {
        Change change = event.getChange();
        if (change.getValue() == null) {
            eventListeners.cleanup();
        }
    }
}
