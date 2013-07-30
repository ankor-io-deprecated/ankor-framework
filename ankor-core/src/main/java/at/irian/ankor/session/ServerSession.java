package at.irian.ankor.session;

import at.irian.ankor.context.ModelContext;
import at.irian.ankor.dispatch.EventDispatcher;
import at.irian.ankor.ref.RefContext;

/**
 * @author Manfred Geiler
 */
public class ServerSession implements Session {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ServerSession.class);

    private final String sessionId;
    private final ModelContext modelContext;
    private final RefContext refContext;
    private final ModelRootFactory modelRootFactory;
    private EventDispatcher eventDispatcher;
    private boolean active;

    public ServerSession(String sessionId,
                         ModelContext modelContext,
                         RefContext refContext,
                         ModelRootFactory modelRootFactory) {
        this.sessionId = sessionId;
        this.modelContext = modelContext;
        this.refContext = refContext;
        this.modelRootFactory = modelRootFactory;
        this.active = false;
    }

    @Override
    public String getId() {
        return sessionId;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void start() {
        Object modelRoot = modelRootFactory.createModelRoot(refContext.refFactory().rootRef());
        refContext.refFactory().rootRef().setValue(modelRoot);
        this.active = true;
    }

    @Override
    public void stop() {
        refContext.refFactory().rootRef().setValue(null);
        eventDispatcher.close();
        this.active = false;
    }


    @Override
    public ModelContext getModelContext() {
        return modelContext;
    }

    @Override
    public RefContext getRefContext() {
        return refContext;
    }

    public void setEventDispatcher(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }
}
