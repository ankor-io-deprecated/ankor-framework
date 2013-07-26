package at.irian.ankor.context;

import at.irian.ankor.event.EventListeners;

/**
 * @author Manfred Geiler
 */
public interface ModelContext {

    EventListeners getModelEventListeners();

    Object getModelRoot();

    void setModelRoot(Object modelRoot);
}
