package at.irian.ankor.context;

import at.irian.ankor.event.EventListeners;
import at.irian.ankor.event.dispatch.EventDispatcher;

import java.util.Map;

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

    Map<String,Object> getAttributes();



    void pushEventDispatcher(EventDispatcher eventDispatcher);

    EventDispatcher popEventDispatcher();

}
