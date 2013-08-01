package at.irian.ankor.session;

import at.irian.ankor.context.ModelContext;
import at.irian.ankor.event.dispatch.EventDispatcherFactory;
import at.irian.ankor.messaging.MessageSenderProvider;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefContextFactory;

/**
 * @author Manfred Geiler
 */
public class ServerSessionFactory implements SessionFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ServerSessionFactory.class);

    private final ModelRootFactory modelRootFactory;
    private final RefContextFactory refContextFactory;
    private final EventDispatcherFactory eventDispatcherFactory;
    private final MessageSenderProvider messageSenderProvider;

    public ServerSessionFactory(ModelRootFactory modelRootFactory,
                                RefContextFactory refContextFactory,
                                EventDispatcherFactory eventDispatcherFactory,
                                MessageSenderProvider messageSenderProvider) {
        this.modelRootFactory = modelRootFactory;
        this.refContextFactory = refContextFactory;
        this.eventDispatcherFactory = eventDispatcherFactory;
        this.messageSenderProvider = messageSenderProvider;
    }

    /**
     * Creates a server session.
     */
    @Override
    public ServerSession create(ModelContext modelContext, RemoteSystem remoteSystem) {
        RefContext refContext = refContextFactory.createRefContextFor(modelContext);
        return new ServerSession(modelContext, refContext, modelRootFactory,
                                 messageSenderProvider.getMessageSenderFor(remoteSystem));
    }

    @Override
    public void close() {
        eventDispatcherFactory.close();
    }
}
