package at.irian.ankor.session;

import at.irian.ankor.context.ModelContext;
import at.irian.ankor.messaging.MessageSender;
import at.irian.ankor.ref.RefContext;

/**
 * @author Manfred Geiler
 */
public class ServerSession implements Session {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ServerSession.class);

    private final ModelContext modelContext;
    private final RefContext refContext;
    private final MessageSender messageSender;

    public ServerSession(ModelContext modelContext,
                         RefContext refContext,
                         MessageSender messageSender) {
        this.modelContext = modelContext;
        this.refContext = refContext;
        this.messageSender = messageSender;
    }

    /**
     * Create the view model root
     */
    @Override
    public void init() {
    }

    @Override
    public void close() {
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
}
