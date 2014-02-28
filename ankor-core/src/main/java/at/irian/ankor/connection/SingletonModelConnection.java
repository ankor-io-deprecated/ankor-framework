package at.irian.ankor.connection;

import at.irian.ankor.session.ModelSession;
import at.irian.ankor.messaging.MessageSender;
import at.irian.ankor.ref.RefContext;

/**
 * Simple ModelConnection, typically used on client systems that handle only one view model at the same time.
 *
 * @author Manfred Geiler
 * @see SingletonModelConnectionManager
 */
public class SingletonModelConnection implements ModelConnection {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SingletonModelConnection.class);

    private final ModelSession modelSession;
    private final RefContext refContext;
    private final MessageSender messageSender;

    public SingletonModelConnection(ModelSession modelSession,
                                    RefContext refContext,
                                    MessageSender messageSender) {
        this.modelSession = modelSession;
        this.refContext = refContext;
        this.messageSender = messageSender;
    }

    @Override
    public void init() {
    }

    @Override
    public void close() {
    }

    @Override
    public ModelSession getModelSession() {
        return modelSession;
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
