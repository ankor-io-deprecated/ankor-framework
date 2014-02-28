package at.irian.ankor.system;

import at.irian.ankor.session.ModelSessionManager;
import at.irian.ankor.messaging.MessageBus;
import at.irian.ankor.messaging.MessageFactory;
import at.irian.ankor.messaging.RemoteMessageListener;
import at.irian.ankor.ref.RefContextFactory;
import at.irian.ankor.connection.ModelConnectionManager;

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
    private final ModelSessionManager modelSessionManager;
    private final ModelConnectionManager modelConnectionManager;
    private final RemoteMessageListener remoteMessageListener;

    protected AnkorSystem(String systemName,
                          MessageFactory messageFactory,
                          MessageBus messageBus,
                          RefContextFactory refContextFactory,
                          ModelSessionManager modelSessionManager,
                          ModelConnectionManager modelConnectionManager,
                          RemoteMessageListener remoteMessageListener) {
        this.systemName = systemName;
        this.messageFactory = messageFactory;
        this.messageBus = messageBus;
        this.refContextFactory = refContextFactory;
        this.modelSessionManager = modelSessionManager;
        this.modelConnectionManager = modelConnectionManager;
        this.remoteMessageListener = remoteMessageListener;
    }

    public String getSystemName() {
        return systemName;
    }

    public MessageBus getMessageBus() {
        return messageBus;
    }

    public RefContextFactory getRefContextFactory() {
        return refContextFactory;
    }

    public ModelConnectionManager getModelConnectionManager() {
        return modelConnectionManager;
    }

    public ModelSessionManager getModelSessionManager() {
        return modelSessionManager;
    }

    @Override
    public String toString() {
        return "AnkorSystem{'" + systemName + "'}";
    }

    public AnkorSystem start() {
        LOG.info("Starting {}", this);
        messageBus.registerMessageListener(remoteMessageListener);
        return this;
    }


    @SuppressWarnings("UnusedDeclaration")
    public void stop() {
        LOG.info("Stopping {}", this);
        messageBus.unregisterMessageListener(remoteMessageListener);
    }

}
