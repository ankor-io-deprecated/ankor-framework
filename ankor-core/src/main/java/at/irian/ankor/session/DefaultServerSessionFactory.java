package at.irian.ankor.session;

import at.irian.ankor.context.DefaultModelContext;
import at.irian.ankor.context.ModelContext;
import at.irian.ankor.messaging.MessageFactory;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefContextFactory;
import at.irian.ankor.system.DefaultSyncActionEventListener;
import at.irian.ankor.system.DefaultSyncChangeEventListener;

/**
 * @author Manfred Geiler
 */
public class DefaultServerSessionFactory implements SessionFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultServerSessionFactory.class);

    private final ModelRootFactory modelRootFactory;
    private final RefContextFactory refContextFactory;
    private final MessageFactory messageFactory;

    public DefaultServerSessionFactory(ModelRootFactory modelRootFactory,
                                       RefContextFactory refContextFactory,
                                       MessageFactory messageFactory) {
        this.modelRootFactory = modelRootFactory;
        this.refContextFactory = refContextFactory;
        this.messageFactory = messageFactory;
    }

    @Override
    public Session create(String sessionId) {

        ModelContext modelContext = new DefaultModelContext();
        RefContext refContext = refContextFactory.createRefContextFor(modelContext);
        Object modelRoot = modelRootFactory.createModelRoot(refContext.refFactory().rootRef());
        modelContext.setModelRoot(modelRoot);
        Session session = new DefaultServerSession(sessionId, modelContext, refContext);

        // action event listener for sending action events to remote partner
        modelContext.getModelEventListeners().add(new DefaultSyncActionEventListener(messageFactory, session));

        // global change event listener for sending change events to remote partner
        modelContext.getModelEventListeners().add(new DefaultSyncChangeEventListener(messageFactory, session));

        return session;
    }
}
