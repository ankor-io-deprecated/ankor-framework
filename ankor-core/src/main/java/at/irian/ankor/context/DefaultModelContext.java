package at.irian.ankor.context;

import at.irian.ankor.event.ArrayListEventListeners;
import at.irian.ankor.event.EventListeners;

/**
 * @author Manfred Geiler
 */
class DefaultModelContext implements ModelContext {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelContext.class);

    private final String id;
    private final EventListeners eventListeners;
    private Object modelRoot;

    DefaultModelContext(String id) {
        this(id, null);
    }

    DefaultModelContext(String id, Object modelRoot) {
        this.id = id;
        this.eventListeners = new ArrayListEventListeners();
        this.modelRoot = modelRoot;
    }

    public String getId() {
        return id;
    }

    @Override
    public EventListeners getEventListeners() {
        return eventListeners;
    }

    @Override
    public Object getModelRoot() {
        return modelRoot;
    }

    @Override
    public void setModelRoot(Object modelRoot) {
        this.modelRoot = modelRoot;
    }

}
