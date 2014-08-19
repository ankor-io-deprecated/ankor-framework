package at.irian.ankor.event.dispatch;

import at.irian.ankor.event.Event;

/**
 * @author Manfred Geiler
 */
public interface EventDispatcher {

    void dispatch(Event event);

    void close();
}
