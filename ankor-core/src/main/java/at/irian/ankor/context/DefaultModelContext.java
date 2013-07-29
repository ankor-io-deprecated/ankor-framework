package at.irian.ankor.context;

import at.irian.ankor.event.ArrayListEventListeners;
import at.irian.ankor.event.EventListeners;

/**
 * @author Manfred Geiler
 */
public class DefaultModelContext implements ModelContext {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelContext.class);

    private final EventListeners eventListeners;
    private Object modelRoot;

    public DefaultModelContext() {
        this(null);
    }

    public DefaultModelContext(Object modelRoot) {
        this.eventListeners = new ArrayListEventListeners();
        this.modelRoot = modelRoot;
    }

    @Override
    public EventListeners getModelEventListeners() {
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
