package at.irian.ankor.dispatch;

import at.irian.ankor.event.EventListeners;
import at.irian.ankor.event.ModelEvent;
import at.irian.ankor.event.ModelEventListener;

/**
 * @author Manfred Geiler
 */
public class SimpleEventDispatcher implements EventDispatcher {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleEventDispatcher.class);

    private final EventListeners eventListeners;

    public SimpleEventDispatcher(EventListeners eventListeners) {
        this.eventListeners = eventListeners;
    }

    @Override
    public void dispatch(ModelEvent event) {
        for (ModelEventListener eventListener : eventListeners) {
            if (event.isAppropriateListener(eventListener)) {
                try {
                    event.processBy(eventListener);
                } catch (Exception e) {
                    LOG.error(String.format("Listener %s threw exception while handling %s", eventListener, event), e);
                }
            }
        }
    }

}
