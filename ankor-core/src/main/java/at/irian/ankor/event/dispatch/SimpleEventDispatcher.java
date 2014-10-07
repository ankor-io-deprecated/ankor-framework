package at.irian.ankor.event.dispatch;

import at.irian.ankor.event.Event;
import at.irian.ankor.event.EventListeners;
import at.irian.ankor.event.EventListener;

/**
 * Simple thread-unsafe(!) EventDispatcher implementation that just calls all appropriate listeners for a given event.
 *
 * @author Manfred Geiler
 */
public class SimpleEventDispatcher implements EventDispatcher {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleEventDispatcher.class);

    private final EventListeners eventListeners;

    public SimpleEventDispatcher(EventListeners eventListeners) {
        this.eventListeners = eventListeners;
    }

    @Override
    public void dispatch(Event event) {
        for (EventListener eventListener : eventListeners) {
            if (event.isAppropriateListener(eventListener)) {
                try {
                    event.processBy(eventListener);
                } catch (Exception e) {
                    LOG.error(String.format("Listener %s threw exception while handling %s", eventListener, event), e);
                }
            }
        }
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void close() {
    }
}
