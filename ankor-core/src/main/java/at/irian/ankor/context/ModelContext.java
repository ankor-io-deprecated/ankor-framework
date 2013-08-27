package at.irian.ankor.context;

import at.irian.ankor.event.EventListeners;
import at.irian.ankor.event.dispatch.EventDispatcher;

/**
 * @author Manfred Geiler
 */
public interface ModelContext {

    String getId();

    EventListeners getEventListeners();

    Object getModelRoot(String rootName);

    void setModelRoot(String rootName, Object modelRoot);

    EventDispatcher getEventDispatcher();

    void close();
}
