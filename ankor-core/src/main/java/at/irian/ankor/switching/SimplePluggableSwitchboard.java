package at.irian.ankor.switching;

import at.irian.ankor.switching.connector.*;
import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.routing.ModelAddress;
import at.irian.ankor.switching.routing.ConcurrentRoutingTable;
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

    private enum Status {
        INITIALIZED,
        STARTING,
        RUNNING,
        STOPPING,
        STOPPED
    }

    private RoutingLogic routingLogic;
    private final Map<Class<? extends ModelAddress>, ConnectionHandler<? extends ModelAddress>> connectionHandlers = new ConcurrentHashMap<Class<? extends ModelAddress>, ConnectionHandler<? extends ModelAddress>>();
    private final Map<Class<? extends ModelAddress>, TransmissionHandler<? extends ModelAddress>> transmissionHandlers = new ConcurrentHashMap<Class<? extends ModelAddress>, TransmissionHandler<? extends ModelAddress>>();
    private final RoutingTable routingTable = new ConcurrentRoutingTable();
    private volatile Status status = Status.INITIALIZED;

    @Override
    public ConnectorPlug getConnectorPlug() {
        return this;
    }

    @Override
    public void setRoutingLogic(RoutingLogic routingLogic) {
        checkNotRunning();
        if (this.routingLogic != null) {
            throw new IllegalStateException("RoutingLogic already set");
        }
        this.routingLogic = routingLogic;
    }

    @Override
    public void registerConnectionHandler(Class<? extends ModelAddress> receiverAddressType,
                                          ConnectionHandler<? extends ModelAddress> connectionHandler) {
        checkNotRunning();
        if (connectionHandlers.put(receiverAddressType, connectionHandler) != null) {
            throw new IllegalStateException("ConnectionHandler for address type " + receiverAddressType.getName() + " already registered");
        }
    }

    @Override
    public void registerTransmissionHandler(Class<? extends ModelAddress> receiverAddressType,
                                            TransmissionHandler<? extends ModelAddress> transmissionHandler) {
        checkNotRunning();
        if (transmissionHandlers.put(receiverAddressType, transmissionHandler) != null) {
            throw new IllegalStateException("TransmissionHandler for address type " + receiverAddressType.getName() + " already registered");
        }
    }

    @Override
    public void unregisterConnectionHandler(Class<? extends ModelAddress> receiverAddressType) {
        checkNotRunning();
        if (connectionHandlers.remove(receiverAddressType) == null) {
            LOG.warn("ConnectionHandler for address type " + receiverAddressType.getName() + " was not registered");
        }
    }

    @Override
    public void unregisterTransmissionHandler(Class<? extends ModelAddress> receiverAddressType) {
        checkNotRunning();
        if (transmissionHandlers.remove(receiverAddressType) == null) {
            LOG.warn("TransmissionHandler for address type " + receiverAddressType.getName() + " was not registered");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void openConnection(ModelAddress sender, Map<String, Object> connectParameters) {
        checkRunning();

        // find route
        ModelAddress receiver = routingLogic.findRoutee(sender, connectParameters);
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
    public void send(ModelAddress sender, EventMessage message) {
        checkRunning();
        Set<ModelAddress> alreadyDelivered = new HashSet<ModelAddress>(); // todo  optimze for 99% one-to-one routings (with Guava?)
        alreadyDelivered.add(sender);
        sendRecursive(sender, sender, message, alreadyDelivered);
    }

    protected void sendRecursive(ModelAddress originalSender,
                                 ModelAddress sender,
                                 EventMessage message,
                                 Set<ModelAddress> alreadyDelivered) {
        Collection<ModelAddress> receivers = routingTable.getConnectedAddresses(sender);
        for (ModelAddress receiver : receivers) {
            if (!alreadyDelivered.contains(receiver)) {
                send(originalSender, receiver, message);
                alreadyDelivered.add(receiver);
                sendRecursive(originalSender, receiver, message, alreadyDelivered);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void send(ModelAddress sender, ModelAddress receiver, EventMessage message) {
        checkRunningOrStopping();
        getTransmissionHandler(receiver).transmitEventMessage(sender, receiver, message);
    }


    @SuppressWarnings("unchecked")
    @Override
    public void closeAllConnections(ModelAddress sender) {
        checkRunningOrStopping();

        Collection<ModelAddress> receivers = routingTable.getConnectedAddresses(sender);
        for (ModelAddress receiver : receivers) {
            closeConnection(sender, receiver);
        }
    }

    @Override
    public void closeConnection(ModelAddress sender, ModelAddress receiver) {
        checkRunningOrStopping();

        LOG.debug("Remove route between {} and {}", sender, receiver);
        routingTable.disconnect(sender, receiver);

        closeDirectedConnection(sender, receiver);
        closeDirectedConnection(receiver, sender);
    }


    @SuppressWarnings("unchecked")
    protected void closeDirectedConnection(ModelAddress sender, ModelAddress receiver) {
        boolean noMoreRouteToReceiver = !routingTable.hasConnectedAddresses(receiver);
        getConnectionHandler(receiver).closeConnection(sender, receiver, noMoreRouteToReceiver);
    }


    @Override
    public void start() {
        this.status = Status.RUNNING;
    }

    @Override
    public void stop() {
        this.status = Status.STOPPING;
        for (ModelAddress p : routingTable.getAllConnectedAddresses()) {
            closeAllConnections(p);
        }
        routingTable.clear();
        this.status = Status.STOPPED;
    }

    private void checkRunning() {
        if (status != Status.RUNNING) {
            throw new IllegalStateException("Switchboard is " + status);
        }
    }

    private void checkRunningOrStopping() {
        if (status != Status.RUNNING && status != Status.STOPPING) {
            throw new IllegalStateException("Switchboard is " + status);
        }
    }

    private void checkNotRunning() {
        if (status == Status.RUNNING || status == Status.STOPPING) {
            throw new IllegalStateException("Switchboard is " + status);
        }
    }

    private ConnectionHandler getConnectionHandler(ModelAddress receiver) {
        Class<? extends ModelAddress> addressType = receiver.getClass();
        ConnectionHandler connectionHandler = connectionHandlers.get(addressType);
        if (connectionHandler == null) {
            throw new IllegalStateException("No ConnectionHandler found for address type " + addressType);
        }
        return connectionHandler;
    }

    private TransmissionHandler getTransmissionHandler(ModelAddress receiver) {
        Class<? extends ModelAddress> addressType = receiver.getClass();
        TransmissionHandler transmissionHandler = transmissionHandlers.get(addressType);
        if (transmissionHandler == null) {
            throw new IllegalStateException("No TransmissionHandler found for address type " + addressType);
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
