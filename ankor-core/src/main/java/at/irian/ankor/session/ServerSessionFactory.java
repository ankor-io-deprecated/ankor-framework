package at.irian.ankor.session;

import at.irian.ankor.context.ModelContext;
import at.irian.ankor.event.dispatch.EventDispatcherFactory;
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

    public ServerSessionFactory(ModelRootFactory modelRootFactory,
                                RefContextFactory refContextFactory,
                                EventDispatcherFactory eventDispatcherFactory) {
        this.modelRootFactory = modelRootFactory;
        this.refContextFactory = refContextFactory;
        this.eventDispatcherFactory = eventDispatcherFactory;
    }

    /**
     * Creates a server session.
     */
    @Override
    public ServerSession create(ModelContext modelContext, String sessionId) {
        RefContext refContext = refContextFactory.createRefContextFor(modelContext);
        return new ServerSession(sessionId, modelContext, refContext, modelRootFactory);
    }

    @Override
    public void close() {
        eventDispatcherFactory.close();
    }
}
