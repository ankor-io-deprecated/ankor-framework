package at.irian.ankor.switching;

import at.irian.ankor.switching.connector.*;
import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.party.Party;
import at.irian.ankor.switching.routing.DefaultRoutingTable;
import at.irian.ankor.switching.routing.RoutingLogic;
import at.irian.ankor.switching.routing.RoutingTable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Manfred Geiler
 */
public class SimplePluggableSwitchboard implements Switchboard, PluggableSwitchboard, ConnectorPlug {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimplePluggableSwitchboard.class);

    private RoutingLogic routingLogic;
    private final Map<Class<? extends Party>, ConnectionHandler<? extends Party>> connectionHandlers = new ConcurrentHashMap<Class<? extends Party>, ConnectionHandler<? extends Party>>();
    private final Map<Class<? extends Party>, TransmissionHandler<? extends Party>> transmissionHandlers = new ConcurrentHashMap<Class<? extends Party>, TransmissionHandler<? extends Party>>();
    private final RoutingTable routingTable = new DefaultRoutingTable();
    private volatile boolean started = false;

    @Override
    public ConnectorPlug getConnectorPlug() {
        return this;
    }

    @Override
    public void setRoutingLogic(RoutingLogic routingLogic) {
        if (this.routingLogic != null) {
            throw new IllegalStateException("RoutingLogic already set");
        }
        this.routingLogic = routingLogic;
    }

    @Override
    public void registerConnectionHandler(Class<? extends Party> receiverPartyType,
                                          ConnectionHandler<? extends Party> connectionHandler) {
        if (connectionHandlers.put(receiverPartyType, connectionHandler) != null) {
            throw new IllegalStateException("ConnectionHandler for party type " + receiverPartyType.getName() + " already registered");
        }
    }

    @Override
    public void registerTransmissionHandler(Class<? extends Party> receiverPartyType,
                                            TransmissionHandler<? extends Party> transmissionHandler) {
        if (transmissionHandlers.put(receiverPartyType, transmissionHandler) != null) {
            throw new IllegalStateException("TransmissionHandler for party type " + receiverPartyType.getName() + " already registered");
        }
    }

    @Override
    public void unregisterConnectionHandler(Class<? extends Party> receiverPartyType) {
        if (connectionHandlers.remove(receiverPartyType) == null) {
            LOG.warn("ConnectionHandler for party type " + receiverPartyType.getName() + " was not registered");
        }
    }

    @Override
    public void unregisterTransmissionHandler(Class<? extends Party> receiverPartyType) {
        if (transmissionHandlers.remove(receiverPartyType) == null) {
            LOG.warn("TransmissionHandler for party type " + receiverPartyType.getName() + " was not registered");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void openConnection(Party sender, Map<String, Object> connectParameters) {
        checkStarted();

        // find route
        Party receiver = routingLogic.findRoutee(sender, connectParameters);
        if (receiver == null) {
            LOG.info("Connect request from {} with params {} was not accepted", sender, connectParameters);
            return;
        }

        // add route
        boolean success = routingTable.connect(sender, receiver);
        if (!success) {
            throw new IllegalStateException("Already connected: " + sender + " and " + receiver);
        }

        // open connection
        getConnectionHandler(receiver).openConnection(sender, receiver, connectParameters);
    }


    @Override
    public void send(Party sender, EventMessage message) {
        checkStarted();
        Set<Party> alreadyDelivered = new HashSet<Party>(); // todo  optimze for 99% one-to-one routings (with Guava?)
        alreadyDelivered.add(sender);
        sendRecursive(sender, sender, message, alreadyDelivered);
    }

    protected void sendRecursive(Party originalSender,
                                 Party sender,
                                 EventMessage message,
                                 Set<Party> alreadyDelivered) {
        Collection<Party> receivers = routingTable.getConnectedParties(sender);
        for (Party receiver : receivers) {
            if (!alreadyDelivered.contains(receiver)) {
                send(originalSender, receiver, message);
                alreadyDelivered.add(receiver);
                sendRecursive(originalSender, receiver, message, alreadyDelivered);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void send(Party sender, Party receiver, EventMessage message) {
        checkStarted();
        getTransmissionHandler(receiver).transmitEventMessage(sender, receiver, message);
    }


    @SuppressWarnings("unchecked")
    @Override
    public void closeAllConnections(Party sender) {
        checkStarted();

        Collection<Party> receivers = routingTable.getConnectedParties(sender);
        for (Party receiver : receivers) {
            closeConnection(sender, receiver);
        }
    }

    @Override
    public void closeConnection(Party sender, Party receiver) {
        checkStarted();

        LOG.debug("Remove route between {} and {}", sender, receiver);
        routingTable.disconnect(sender, receiver);

        closeDirectedConnection(sender, receiver);
        closeDirectedConnection(receiver, sender);
    }


    @SuppressWarnings("unchecked")
    protected void closeDirectedConnection(Party sender, Party receiver) {
        boolean noMoreRouteToReceiver = !routingTable.hasConnectedParties(receiver);
        getConnectionHandler(receiver).closeConnection(sender, receiver, noMoreRouteToReceiver);
    }


    @Override
    public void start() {
        this.started = true;
    }

    @Override
    public void stop() {
        this.started = false;
    }

    private void checkStarted() {
        if (!started) {
            throw new IllegalStateException("Switchboard not started");
        }
    }

    private ConnectionHandler getConnectionHandler(Party receiver) {
        Class<? extends Party> partyType = receiver.getClass();
        ConnectionHandler connectionHandler = connectionHandlers.get(partyType);
        if (connectionHandler == null) {
            throw new IllegalStateException("No ConnectionHandler found for party type " + partyType);
        }
        return connectionHandler;
    }

    private TransmissionHandler getTransmissionHandler(Party receiver) {
        Class<? extends Party> partyType = receiver.getClass();
        TransmissionHandler transmissionHandler = transmissionHandlers.get(partyType);
        if (transmissionHandler == null) {
            throw new IllegalStateException("No TransmissionHandler found for party type " + partyType);
        }
        return transmissionHandler;
    }



    public static class Factory implements PluggableSwitchboardFactory {
        @Override
        public PluggableSwitchboard createSwitchboard() {
            return new SimplePluggableSwitchboard();
        }
    }
}
