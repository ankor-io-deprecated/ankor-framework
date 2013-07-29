package at.irian.ankor.context;

import at.irian.ankor.event.EventListeners;

/**
 * @author Manfred Geiler
 */
public interface ModelContext {

    EventListeners getEventListeners();

    Object getModelRoot();

    void setModelRoot(Object modelRoot);
}
