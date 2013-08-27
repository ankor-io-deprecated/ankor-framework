package at.irian.ankor.system;

import at.irian.ankor.context.ModelContextManager;
import at.irian.ankor.messaging.MessageBus;
import at.irian.ankor.messaging.MessageFactory;
import at.irian.ankor.messaging.MessageListener;
import at.irian.ankor.ref.RefContextFactory;
import at.irian.ankor.session.ModelRootFactory;
import at.irian.ankor.session.SessionManager;

/**
 * This is the main system object that sticks all the Ankor parts together.
 * Typically every node in an Ankor environment has exactly one AnkorSystem instance.
 * So, in a pure-Java client-server environment (e.g. JavaFX client and Java web server) there is
 * one AnkorSystem instance on the client side and one AnkorSystem instance on the server side.
 *
 * @author Manfred Geiler
 */
@SuppressWarnings("UnusedDeclaration")
public class AnkorSystem {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorSystem.class);

    private final String systemName;
    private final MessageFactory messageFactory;
    private final MessageBus messageBus;
    private final RefContextFactory refContextFactory;
    private final ModelContextManager modelContextManager;
    private final SessionManager sessionManager;
    private final MessageListener messageListener;

    protected AnkorSystem(String systemName,
                          MessageFactory messageFactory,
                          MessageBus messageBus,
                          RefContextFactory refContextFactory,
                          ModelContextManager modelContextManager,
                          SessionManager sessionManager,
                          ModelRootFactory modelRootFactory) {
        this.systemName = systemName;
        this.messageFactory = messageFactory;
        this.messageBus = messageBus;
        this.refContextFactory = refContextFactory;
        this.modelContextManager = modelContextManager;
        this.sessionManager = sessionManager;
        this.messageListener = new DefaultMessageListener(modelContextManager, sessionManager, modelRootFactory);
    }

    public String getSystemName() {
        return systemName;
    }

    @Deprecated
    public MessageFactory getMessageFactory() {
        return messageFactory;
    }

    public MessageBus getMessageBus() {
        return messageBus;
    }

    public RefContextFactory getRefContextFactory() {
        return refContextFactory;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public ModelContextManager getModelContextManager() {
        return modelContextManager;
    }

    @Override
    public String toString() {
        return "AnkorSystem{'" + systemName + "'}";
    }

    public void start() {
        LOG.info("Starting {}", this);
        messageBus.registerMessageListener(messageListener);
    }


    @SuppressWarnings("UnusedDeclaration")
    public void stop() {
        LOG.info("Stopping {}", this);
        messageBus.unregisterMessageListener(messageListener);
    }

}
