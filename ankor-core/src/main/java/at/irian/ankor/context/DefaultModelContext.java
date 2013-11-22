package at.irian.ankor.context;

import at.irian.ankor.event.ArrayListEventListeners;
import at.irian.ankor.event.EventListeners;
import at.irian.ankor.event.dispatch.DispatchThreadAware;
import at.irian.ankor.event.dispatch.EventDispatcher;
import at.irian.ankor.event.dispatch.EventDispatcherFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
class DefaultModelContext implements ModelContext, DispatchThreadAware {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelContext.class);

    private final String id;
    private final EventListeners eventListeners;
    private EventDispatcher eventDispatcher;
    private Map<String, Object> modelRoots;
    private Map<String, Object> attributes;

    private volatile Thread dispatchThread;

    DefaultModelContext(String id, EventListeners eventListeners) {
        this.id = id;
        this.eventListeners = eventListeners;
        this.modelRoots = new HashMap<String, Object>();
        this.attributes = null;
    }

    public static ModelContext create(EventDispatcherFactory eventDispatcherFactory,
                                      String id,
                                      EventListeners globalEventListeners) {
        EventListeners eventListeners = new ArrayListEventListeners(globalEventListeners);
        DefaultModelContext modelContext = new DefaultModelContext(id, eventListeners);
        modelContext.setEventDispatcher(eventDispatcherFactory.createFor(modelContext));
        return modelContext;
    }

    public String getId() {
        return id;
    }

    @Override
    public EventListeners getEventListeners() {
        return eventListeners;
    }

    @Override
    public Object getModelRoot(String rootName) {
        return modelRoots.get(rootName);
    }

    @Override
    public void setModelRoot(String rootName, Object modelRoot) {
        this.modelRoots.put(rootName, modelRoot);
    }

    private void setEventDispatcher(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    @Override
    public void close() {
        if (eventDispatcher != null) {
            eventDispatcher.close();
        }
        modelRoots.clear();
    }

    @Override
    public String toString() {
        return "DefaultModelContext{" +
               "id='" + id + '\'' +
               '}';
    }

    @Override
    public void setCurrentDispatchThread(Thread dispatchThread) {
        this.dispatchThread = dispatchThread;
    }

    @Override
    public Thread getCurrentDispatchThread() {
        return dispatchThread;
    }

    public Map<String, Object> getAttributes() {
        if (attributes == null) {
            //todo  do we have a concurrency issue here?
            attributes = new HashMap<String, Object>();
        }
        return attributes;
    }
}
