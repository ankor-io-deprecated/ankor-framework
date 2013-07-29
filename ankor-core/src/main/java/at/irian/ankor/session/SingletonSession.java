package at.irian.ankor.session;

import at.irian.ankor.context.ModelContext;
import at.irian.ankor.messaging.MessageSender;
import at.irian.ankor.ref.RefContext;

/**
 * @author Manfred Geiler
 */
public class SingletonSession implements Session {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultServerSession.class);

    private String sessionId;
    private final ModelContext modelContext;
    private final RefContext refContext;
    private MessageSender messageSender;

    public SingletonSession(ModelContext modelContext, RefContext refContext) {
        this.modelContext = modelContext;
        this.refContext = refContext;
    }

    @Override
    public String getId() {
        return sessionId;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
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
    public MessageSender getMessageSender() {
        return messageSender;
    }

    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }
}
