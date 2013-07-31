package at.irian.ankor.session;

import at.irian.ankor.context.DefaultModelContext;
import at.irian.ankor.context.ModelContext;
import at.irian.ankor.event.dispatch.EventDispatcher;
import at.irian.ankor.event.dispatch.EventDispatcherFactory;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefContextFactory;
import at.irian.ankor.ref.impl.RefContextImplementor;

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
     * Creates a server session, but without a dispatcher. Derived implementations must set a proper dispatcher.
     */
    @Override
    public ServerSession create(String sessionId) {

        ModelContext modelContext = new DefaultModelContext();
        RefContext refContext = refContextFactory.createRefContextFor(modelContext);

        ServerSession session = new ServerSession(sessionId, modelContext, refContext, modelRootFactory);

        EventDispatcher eventDispatcher = eventDispatcherFactory.createFor(session);
        session.setEventDispatcher(eventDispatcher);

        ((RefContextImplementor)refContext).setSession(session);

        return session;
    }
}
