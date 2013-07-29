package at.irian.ankor.dispatch;

import at.irian.ankor.event.ModelEvent;

/**
 * @author Manfred Geiler
 */
public interface EventDispatcher {

    void dispatch(ModelEvent event);

}
