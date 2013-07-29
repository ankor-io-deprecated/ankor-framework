package at.irian.ankor.session;

import at.irian.ankor.context.ModelContext;
import at.irian.ankor.dispatch.Dispatcher;
import at.irian.ankor.messaging.MessageSender;
import at.irian.ankor.ref.RefContext;

/**
 * @author Manfred Geiler
 */
public class DefaultServerSession implements Session {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultServerSession.class);

    private final String sessionId;
    private final ModelContext modelContext;
    private final RefContext refContext;
    private final ModelRootFactory modelRootFactory;
    private Dispatcher dispatcher;
    private MessageSender messageSender;
    private boolean active;

    public DefaultServerSession(String sessionId,
                                ModelContext modelContext,
                                RefContext refContext,
                                ModelRootFactory modelRootFactory) {
        this.sessionId = sessionId;
        this.modelContext = modelContext;
        this.refContext = refContext;
        this.modelRootFactory = modelRootFactory;
        this.messageSender = null;
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

    @Override
    public MessageSender getMessageSender() {
        return messageSender;
    }

    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    protected void setDispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public Dispatcher getDispatcher() {
        return dispatcher;
    }
}
