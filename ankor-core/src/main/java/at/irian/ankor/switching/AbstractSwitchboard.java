package at.irian.ankor.switching;

import at.irian.ankor.monitor.Monitor;
import at.irian.ankor.switching.connector.ConnectorMapping;
import at.irian.ankor.switching.connector.HandlerScopeContext;
import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.routing.ModelAddress;
import at.irian.ankor.switching.routing.RoutingLogic;
import at.irian.ankor.switching.routing.RoutingTable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Manfred Geiler
 */
public abstract class AbstractSwitchboard implements Switchboard {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AbstractSwitchboard.class);

    private enum Status {
        INITIALIZED,
        STARTING,
        RUNNING,
        STOPPING,
        STOPPED
    }

    private final RoutingTable routingTable;
    private final ConnectorMapping connectorMapping;
    private final HandlerScopeContext handlerScopeContext;
    protected final Monitor monitor;
    private volatile RoutingLogic routingLogic;
    private volatile Status status = Status.INITIALIZED;

    public AbstractSwitchboard(RoutingTable routingTable,
                               ConnectorMapping connectorMapping,
                               HandlerScopeContext handlerScopeContext, Monitor monitor) {
        this.routingTable = routingTable;
        this.connectorMapping = connectorMapping;
        this.handlerScopeContext = handlerScopeContext;
        this.monitor = monitor;
    }

    protected void setRoutingLogic(RoutingLogic routingLogic) {
        checkNotRunning();
        this.routingLogic = routingLogic;
    }

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
            LOG.warn("Already connected: {} and {}", sender, receiver);
            return;
        }

        // open connection
        handleOpenConnection(sender, connectParameters, receiver);
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
                dispatchableSend(originalSender, receiver, message);
                alreadyDelivered.add(receiver);
                sendRecursive(originalSender, receiver, message, alreadyDelivered);
            }
        }
    }

    public void send(ModelAddress sender, EventMessage message, ModelAddress receiver) {
        checkRunningOrStopping();
        handleTransmitEventMessage(sender, receiver, message);
    }

    @Override
    public void closeAllConnections(ModelAddress sender) {
        checkRunningOrStopping();

        Collection<ModelAddress> receivers = routingTable.getConnectedAddresses(sender);
        for (ModelAddress receiver : receivers) {
            dispatchableCloseConnection(sender, receiver);
        }
    }

    @Override
    public void closeConnection(ModelAddress sender, ModelAddress receiver) {
        checkRunningOrStopping();

        LOG.debug("Remove route between {} and {}", sender, receiver);
        routingTable.disconnect(sender, receiver);

        boolean noMoreRouteToReceiver = !routingTable.hasConnectedAddresses(receiver);
        handleCloseConnection(sender, receiver, noMoreRouteToReceiver);
    }

    @Override
    public void start() {
        if (this.routingLogic == null) {
            throw new IllegalStateException("No RoutingLogic");
        }
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

    protected void checkRunning() {
        if (status != Status.RUNNING) {
            throw new IllegalStateException("Switchboard is " + status);
        }
    }

    protected void checkRunningOrStopping() {
        if (status != Status.RUNNING && status != Status.STOPPING) {
            throw new IllegalStateException("Switchboard is " + status);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    protected void checkNotRunning() {
        if (status == Status.RUNNING || status == Status.STOPPING) {
            throw new IllegalStateException("Switchboard is " + status);
        }
    }


    protected abstract void dispatchableSend(ModelAddress sender, ModelAddress receiver, EventMessage message);

    protected abstract void dispatchableCloseConnection(ModelAddress sender, ModelAddress receiver);


    @SuppressWarnings("unchecked")
    protected void handleOpenConnection(ModelAddress sender,
                                        Map<String, Object> connectParameters,
                                        ModelAddress receiver) {
        connectorMapping.getConnectionHandler(receiver).openConnection(sender,
                                                                       receiver,
                                                                       connectParameters,
                                                                       handlerScopeContext);
    }

    @SuppressWarnings("unchecked")
    protected void handleTransmitEventMessage(ModelAddress sender, ModelAddress receiver, EventMessage message) {
        connectorMapping.getTransmissionHandler(receiver).transmitEventMessage(sender,
                                                                               receiver,
                                                                               message,
                                                                               handlerScopeContext);
        monitor.send(sender, message, receiver);
    }

    @SuppressWarnings("unchecked")
    protected void handleCloseConnection(ModelAddress sender, ModelAddress receiver, boolean noMoreRouteToReceiver) {
        connectorMapping.getConnectionHandler(receiver).closeConnection(sender,
                                                                        receiver,
                                                                        noMoreRouteToReceiver,
                                                                        handlerScopeContext);
    }

}
