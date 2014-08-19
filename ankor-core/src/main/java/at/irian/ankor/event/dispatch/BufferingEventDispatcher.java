package at.irian.ankor.event.dispatch;

import at.irian.ankor.event.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Manfred Geiler
 */
public class BufferingEventDispatcher implements EventDispatcher {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BufferingEventDispatcher.class);

    private List<Event> bufferedEvents = null;

    @Override
    public void dispatch(Event event) {
        if (bufferedEvents == null) {
            bufferedEvents = new ArrayList<Event>();
        }
        bufferedEvents.add(event);
    }

    @Override
    public void close() {
        bufferedEvents = null;
    }

    public List<Event> getBufferedEvents() {
        return bufferedEvents != null ? bufferedEvents : Collections.<Event>emptyList();
    }
}
