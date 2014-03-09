package at.irian.ankor.session;

import at.irian.ankor.application.Application;
import at.irian.ankor.event.ArrayListEventListeners;
import at.irian.ankor.event.EventListeners;
import at.irian.ankor.event.dispatch.DispatchThreadAware;
import at.irian.ankor.event.dispatch.EventDispatcher;
import at.irian.ankor.event.dispatch.EventDispatcherFactory;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefContextFactory;

import java.util.*;

/**
 * @author Manfred Geiler
 */
class DefaultModelSession implements ModelSession, DispatchThreadAware {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelSession.class);

    private final String id;
    private final EventListeners eventListeners;
    private final Deque<EventDispatcher> eventDispatcherStack;
    private final Application application;
    private final Map<String, Object> modelRootMap;
    private Map<String, Object> attributes;
    private RefContext refContext;

    private volatile Thread dispatchThread;

    DefaultModelSession(String modelSessionId,
                        EventListeners eventListeners,
                        Application application) {
        this.id = modelSessionId;
        this.eventListeners = eventListeners;
        this.application = application;
        this.modelRootMap = new HashMap<String, Object>();
        this.eventDispatcherStack = new ArrayDeque<EventDispatcher>();
        this.attributes = null;
        this.refContext = null;
    }

    public static ModelSession create(EventDispatcherFactory eventDispatcherFactory,
                                      EventListeners defaultEventListeners,
                                      RefContextFactory refContextFactory,
                                      Application application) {
        EventListeners eventListeners = new ArrayListEventListeners(defaultEventListeners);
        DefaultModelSession modelSession = new DefaultModelSession(createModelSessionId(),
                                                                   eventListeners,
                                                                   application);
        modelSession.refContext = refContextFactory.createRefContextFor(modelSession);
        modelSession.pushEventDispatcher(eventDispatcherFactory.createFor(modelSession));
        return modelSession;
    }

    protected static String createModelSessionId() {
        return UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    @Override
    public EventListeners getEventListeners() {
        return eventListeners;
    }

    @Override
    public void pushEventDispatcher(EventDispatcher eventDispatcher) {
        eventDispatcherStack.push(eventDispatcher);
    }

    @Override
    public EventDispatcher popEventDispatcher() {
        return eventDispatcherStack.pop();
    }

    @Override
    public EventDispatcher getEventDispatcher() {
        return eventDispatcherStack.peek();
    }

    @Override
    public void close() {
        for (EventDispatcher eventDispatcher : eventDispatcherStack) {
            eventDispatcher.close();
        }

        for (Map.Entry<String, Object> entry : modelRootMap.entrySet()) {
            application.releaseModel(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public String toString() {
        return "DefaultModelSession{" +
               "id='" + id + '\'' +
               '}';
    }

    @Override
    public void setCurrentDispatchThread(Thread dispatchThread) {
        this.dispatchThread = dispatchThread;
        LOG.trace("Current dispatch thread for {} is now: {}", this, dispatchThread);
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

    @Override
    public RefContext getRefContext() {
        return refContext;
    }

    @Override
    public void addModelRoot(String modelName, Object modelRoot) {
        modelRootMap.put(modelName, modelRoot);
    }

    @Override
    public Object getModelRoot(String modelName) {
        Object modelRoot = modelRootMap.get(modelName);
        if (modelRoot == null) {
            throw new IllegalArgumentException("No model with name " + modelName);
        }
        return modelRoot;
    }

    @Override
    public Collection<String> getModelNames() {
        // todo  concurrency issue?
        return modelRootMap.keySet();
    }
}
