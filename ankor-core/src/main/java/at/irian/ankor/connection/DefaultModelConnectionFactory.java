package at.irian.ankor.connection;

import at.irian.ankor.context.ModelContext;
import at.irian.ankor.event.dispatch.EventDispatcherFactory;
import at.irian.ankor.messaging.MessageSenderProvider;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefContextFactory;

/**
 * @author Manfred Geiler
 */
public class DefaultModelConnectionFactory implements ModelConnectionFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultModelConnectionFactory.class);

    private final RefContextFactory refContextFactory;
    private final EventDispatcherFactory eventDispatcherFactory;
    private final MessageSenderProvider messageSenderProvider;

    public DefaultModelConnectionFactory(RefContextFactory refContextFactory,
                                         EventDispatcherFactory eventDispatcherFactory,
                                         MessageSenderProvider messageSenderProvider) {
        this.refContextFactory = refContextFactory;
        this.eventDispatcherFactory = eventDispatcherFactory;
        this.messageSenderProvider = messageSenderProvider;
    }

    /**
     * Creates a connection between the ModelContext and the given remote system.
     */
    @Override
    public DefaultModelConnection create(ModelContext modelContext, RemoteSystem remoteSystem) {
        RefContext refContext = refContextFactory.createRefContextFor(modelContext);
        return new DefaultModelConnection(modelContext, refContext,
                                 messageSenderProvider.getMessageSenderFor(remoteSystem));
    }

    @Override
    public void close() {
        eventDispatcherFactory.close();
    }
}
