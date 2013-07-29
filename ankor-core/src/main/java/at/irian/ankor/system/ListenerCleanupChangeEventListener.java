package at.irian.ankor.system;

import at.irian.ankor.change.Change;
import at.irian.ankor.change.ChangeEvent;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.event.EventListeners;

/**
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
    public void process(ChangeEvent event) {

        //todo/check  called twice on remote change?

        Change change = event.getChange();
        if (change.getNewValue() == null) {
            eventListeners.cleanup();
        }
    }
}
