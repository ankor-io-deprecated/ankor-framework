package at.irian.ankor.connector.local;

import at.irian.ankor.application.Application;
import at.irian.ankor.application.ApplicationInstance;
import at.irian.ankor.change.Change;
import at.irian.ankor.event.source.ModelSource;
import at.irian.ankor.msg.ChangeEventMessage;
import at.irian.ankor.msg.ConnectRequestMessage;
import at.irian.ankor.msg.MessageBus;
import at.irian.ankor.msg.RoutingTable;
import at.irian.ankor.msg.party.Party;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.session.ModelSessionManager;

import java.util.Collections;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
class LocalConnectRequestMessageListener implements ConnectRequestMessage.Listener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LocalConnectRequestMessageListener.class);

    private final ModelSessionManager modelSessionManager;
    private final RoutingTable routingTable;
    private final Application application;
    private final MessageBus messageBus;

    public LocalConnectRequestMessageListener(ModelSessionManager modelSessionManager,
                                              RoutingTable routingTable,
                                              Application application,
                                              MessageBus messageBus) {
        this.modelSessionManager = modelSessionManager;
        this.routingTable = routingTable;
        this.application = application;
        this.messageBus = messageBus;
    }

    @Override
    public void onConnectRequest(ConnectRequestMessage msg) {

        Party sender = msg.getSender();
        Map<String,Object> connectParameters = msg.getConnectParameters();
        if (connectParameters == null) {
            connectParameters = Collections.emptyMap();
        }

        LOG.info("Connect message received from {} with parameters {}", sender, connectParameters);

        ApplicationInstance applicationInstance = application.getApplicationInstance(connectParameters);
        if (applicationInstance == null) {
            LOG.info(
                    "Application '{}' did not accept connect parameters {} and returned a null instance - connection denied",
                    application.getName(),
                    connectParameters);
            return;
        }

        String modelName = msg.getModelName();

        ModelSession modelSession = modelSessionManager.getOrCreate(applicationInstance);

        Party receiver = new LocalParty(modelSession.getId(), modelName);

        if (receiver.equals(sender)) {
            throw new IllegalArgumentException("ModelSession must not connect to itself: " + modelSession);
        }

        if (routingTable.isConnected(sender, receiver)) {
            LOG.warn("Already connected: {} and {}", sender, receiver);
        } else {
            LOG.debug("Connecting {} and {}", sender, receiver);
            routingTable.connect(sender, receiver);
        }

        // send an inital change event to the remote party
        messageBus.broadcast(new ChangeEventMessage(receiver,
                                                    new ModelSource(modelSession, modelName, this),
                                                    modelName,
                                                    Change.valueChange(applicationInstance.getModelRoot(modelName))));
    }
}
