package at.irian.ankor.session;

import at.irian.ankor.context.ModelContext;
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
    private MessageSender messageSender;

    public DefaultServerSession(String sessionId,
                                ModelContext modelContext,
                                RefContext refContext) {
        this.sessionId = sessionId;
        this.modelContext = modelContext;
        this.refContext = refContext;
        this.messageSender = null;
    }

    @Override
    public String getId() {
        return sessionId;
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
    public void invalidate() {

    }

    @Override
    public MessageSender getMessageSender() {
        return messageSender;
    }

    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }
}
