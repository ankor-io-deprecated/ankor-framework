package at.irian.ankor.session;

import at.irian.ankor.context.ModelContext;
import at.irian.ankor.dispatch.EventDispatcher;
import at.irian.ankor.ref.Ref;
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

    public ServerSession(String sessionId,
                         ModelContext modelContext,
                         RefContext refContext,
                         ModelRootFactory modelRootFactory) {
        this.sessionId = sessionId;
        this.modelContext = modelContext;
        this.refContext = refContext;
        this.modelRootFactory = modelRootFactory;
    }

    @Override
    public String getId() {
        return sessionId;
    }

    /**
     * Create the view model root
     */
    @Override
    public void init() {
        Ref rootRef = refContext.refFactory().rootRef();
        Object modelRoot = modelRootFactory.createModelRoot(rootRef);
        rootRef.setValue(modelRoot);
    }

    @Override
    public void close() {
        refContext.refFactory().rootRef().setValue(null);
        eventDispatcher.close();
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
