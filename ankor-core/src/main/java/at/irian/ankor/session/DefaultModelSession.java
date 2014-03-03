package at.irian.ankor.session;

import at.irian.ankor.application.ApplicationInstance;
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
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelSession.class);

    private final String id;
    private final EventListeners eventListeners;
    private final Deque<EventDispatcher> eventDispatcherStack;
    private final ApplicationInstance applicationInstance;
    private Map<String, Object> attributes;
    private RefContext refContext;

    private volatile Thread dispatchThread;

    DefaultModelSession(String modelSessionId,
                        ApplicationInstance applicationInstance,
                        EventListeners eventListeners) {
        this.id = modelSessionId;
        this.eventListeners = eventListeners;
        this.applicationInstance = applicationInstance;
        this.eventDispatcherStack = new ArrayDeque<EventDispatcher>();
        this.attributes = null;
        this.refContext = null;
    }

    public static ModelSession create(EventDispatcherFactory eventDispatcherFactory,
                                      EventListeners defaultEventListeners,
                                      ApplicationInstance applicationInstance,
                                      RefContextFactory refContextFactory) {
        EventListeners eventListeners = new ArrayListEventListeners(defaultEventListeners);
        DefaultModelSession modelSession = new DefaultModelSession(UUID.randomUUID().toString(),
                                                                   applicationInstance,
                                                                   eventListeners);
        modelSession.refContext = refContextFactory.createRefContextFor(modelSession);
        modelSession.pushEventDispatcher(eventDispatcherFactory.createFor(modelSession));
        return modelSession;
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
        applicationInstance.release();
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
    public ApplicationInstance getApplicationInstance() {
        return applicationInstance;
    }

    @Override
    public RefContext getRefContext() {
        return refContext;
    }
}
