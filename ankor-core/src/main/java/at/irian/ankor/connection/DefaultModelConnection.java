package at.irian.ankor.connection;

import at.irian.ankor.session.ModelSession;
import at.irian.ankor.event.source.CustomSource;
import at.irian.ankor.messaging.MessageSender;
import at.irian.ankor.ref.RefContext;

/**
 * @author Manfred Geiler
 */
public class DefaultModelConnection implements ModelConnection {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultModelConnection.class);

    private final ModelSession modelSession;
    private final RefContext refContext;
    private final MessageSender messageSender;

    public DefaultModelConnection(ModelSession modelSession,
                                  RefContext refContext,
                                  MessageSender messageSender) {
        this.modelSession = modelSession;
        this.refContext = refContext;
        this.messageSender = messageSender;
    }

    @Override
    public void init() {
        refContext.modelSession().getEventDispatcher().dispatch(new ModelConnectionInitEvent(new CustomSource(this),
                                                                                     this));
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
