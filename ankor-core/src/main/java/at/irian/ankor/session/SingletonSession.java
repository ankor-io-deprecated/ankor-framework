package at.irian.ankor.session;

import at.irian.ankor.context.ModelContext;
import at.irian.ankor.event.dispatch.EventDispatcher;
import at.irian.ankor.event.dispatch.SynchronisedEventDispatcher;
import at.irian.ankor.ref.RefContext;

/**
 * Simple Session, typically used on client systems that handle only one view model at the same time.
 *
 * @author Manfred Geiler
 * @see SingletonSessionManager
 */
public class SingletonSession implements Session {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ServerSession.class);

    private String sessionId;
    private final ModelContext modelContext;
    private final RefContext refContext;
    private final EventDispatcher eventDispatcher;

    public SingletonSession(ModelContext modelContext, RefContext refContext) {
        this.modelContext = modelContext;
        this.refContext = refContext;
        this.eventDispatcher = new SynchronisedEventDispatcher(modelContext);
    }

    @Override
    public String getId() {
        return sessionId;
    }

    @Override
    public void init() {
    }

    @Override
    public void close() {
        eventDispatcher.close();
    }

    public void setId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public ModelContext getModelContext() {
        return modelContext;
    }

    @Override
    public RefContext getRefContext() {
        return refContext;
    }

    @Override
    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }
}
