package at.irian.ankor.event.dispatch;

import at.irian.ankor.event.ModelEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Manfred Geiler
 */
public class BufferingEventDispatcher implements EventDispatcher {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BufferingEventDispatcher.class);

    private List<ModelEvent> bufferedEvents = null;

    @Override
    public void dispatch(ModelEvent event) {
        if (bufferedEvents == null) {
            bufferedEvents = new ArrayList<ModelEvent>();
        }
        bufferedEvents.add(event);
    }

    @Override
    public void close() {
        bufferedEvents = null;
    }

    public List<ModelEvent> getBufferedEvents() {
        return bufferedEvents != null ? bufferedEvents : Collections.<ModelEvent>emptyList();
    }
}
