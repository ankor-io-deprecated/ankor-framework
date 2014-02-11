package at.irian.ankor.context;

import at.irian.ankor.event.EventListeners;
import at.irian.ankor.event.dispatch.EventDispatcher;

import java.util.Map;

/**
 * The ModelContext provides access to all information that belongs to one specific model instance. It represents
 * the model instance during runtime.
 * Every ModelContext has a unique identifier that identifies the model. Besides that the ModelContext provides access
 * to the event listeners of the model and one or more "model roots".
 *
 * @author Manfred Geiler
 */
public interface ModelContext {

    /**
     * @return  the unique identifier of the corresponding model
     */
    String getId();

    /**
     * @return  the event listeners of this model
     */
    EventListeners getEventListeners();

    /**
     * @param rootName  name of the model root variable
     * @return the root object with the given variable name or null if no such root object exists
     */
    Object getModelRoot(String rootName);

    /**
     * Adds a new model root object.
     * @param rootName  name of the model root variable
     * @param modelRoot root of the object tree to be added to the model
     */
    void setModelRoot(String rootName, Object modelRoot);

    /**
     * Close this model and free all resources.
     * After a model is closed, the framework is free to cleanup all resources that might have been used by this
     * model.
     * ModelContext implementations should close the associated EventDispatcher(s) and omit all references to
     * model root instances.
     */
    void close();

    /**
     * Get a custom attributes map that may be used by Ankor extensions.
     * Extensions, that want to store custom attributes for a model, must take care that the map key they use is unique.
     * @return custom attributes map
     */
    Map<String,Object> getAttributes();

    /**
     * @return the event dispatcher that is currently used for this model
     */
    EventDispatcher getEventDispatcher();

    /**
     * Replace the currently used EventDispatcher by the given EventDispatcher. The current dispatcher is
     * put on a stack and may later be restored by a call to {@link #popEventDispatcher()}.
     * @param eventDispatcher the new EventDispatcher
     */
    void pushEventDispatcher(EventDispatcher eventDispatcher);

    /**
     * Omit the current EventDispatcher and restore the previously used EventDispatcher for this model.
     * @return the restored EventDispatcher
     * @throws java.util.NoSuchElementException if there is no previous EventDispatcher
     */
    EventDispatcher popEventDispatcher();

}
