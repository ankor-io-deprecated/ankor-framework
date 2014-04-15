package at.irian.ankor.switching;

import at.irian.ankor.monitor.SwitchboardMonitor;
import at.irian.ankor.switching.connector.ConnectorMapping;
import at.irian.ankor.switching.connector.HandlerScopeContext;
import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.routing.ModelAddress;
import at.irian.ankor.switching.routing.RoutingLogic;

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

    private final ConnectorMapping connectorMapping;
    private final HandlerScopeContext handlerScopeContext;
    private final SwitchboardMonitor switchboardMonitor;
    private volatile RoutingLogic routingLogic;
    private volatile Status status = Status.INITIALIZED;

    public AbstractSwitchboard(ConnectorMapping connectorMapping,
                               HandlerScopeContext handlerScopeContext,
                               SwitchboardMonitor switchboardMonitor) {
        this.connectorMapping = connectorMapping;
        this.handlerScopeContext = handlerScopeContext;
        this.switchboardMonitor = switchboardMonitor;
    }

    protected void setRoutingLogic(RoutingLogic routingLogic) {
        checkNotRunning();
        this.routingLogic = routingLogic;
    }

    @Override
    public void openConnection(ModelAddress sender, Map<String, Object> connectParameters) {
        switchboardMonitor.monitor_openConnection(this, sender, connectParameters);
        checkRunning();

        // find route
        ModelAddress receiver = routingLogic.connect(sender, connectParameters);
        if (receiver == null) {
            LOG.info("Connect request from {} with params {} was not accepted", sender, connectParameters);
            return;
        }

        // open connection
        handleOpenConnection(sender, connectParameters, receiver);
    }

    @Override
    public void send(ModelAddress sender, EventMessage message) {
        switchboardMonitor.monitor_send(this, sender, message);
        checkRunning();
        Set<ModelAddress> alreadyDelivered = new HashSet<ModelAddress>(); // todo  optimze for 99% one-to-one routings (with Guava?)
        alreadyDelivered.add(sender);
        sendRecursive(sender, sender, message, alreadyDelivered);
    }

    protected void sendRecursive(ModelAddress originalSender,
                                 ModelAddress sender,
                                 EventMessage message,
                                 Set<ModelAddress> alreadyDelivered) {
        Collection<ModelAddress> receivers = routingLogic.getConnectedRoutees(sender);
        for (ModelAddress receiver : receivers) {
            if (!alreadyDelivered.contains(receiver)) {
                dispatchableSend(originalSender, receiver, message);
                alreadyDelivered.add(receiver);
                sendRecursive(originalSender, receiver, message, alreadyDelivered);
            }
        }
    }

    public void send(ModelAddress sender, EventMessage message, ModelAddress receiver) {
        switchboardMonitor.monitor_send(this, sender, message, receiver);
        checkRunningOrStopping();
        handleTransmitEventMessage(sender, receiver, message);
    }

    @Override
    public void closeAllConnections(ModelAddress sender) {
        switchboardMonitor.monitor_closeAllConnections(this, sender);
        checkRunningOrStopping();

        Collection<ModelAddress> receivers = routingLogic.getConnectedRoutees(sender);
        for (ModelAddress receiver : receivers) {
            dispatchableCloseConnection(sender, receiver);
        }
    }

    @Override
    public void closeConnection(ModelAddress sender, ModelAddress receiver) {
        switchboardMonitor.monitor_closeConnection(this, sender, receiver);
        checkRunningOrStopping();

        LOG.debug("Remove route between {} and {}", sender, receiver);
        routingLogic.disconnect(sender, receiver);

        boolean noMoreRouteToReceiver = routingLogic.getConnectedRoutees(receiver).isEmpty();
        handleCloseConnection(sender, receiver, noMoreRouteToReceiver);
    }

    @Override
    public void start() {
        switchboardMonitor.monitor_start(this);
        if (this.routingLogic == null) {
            throw new IllegalStateException("No RoutingLogic");
        }
        this.routingLogic.init();
        this.status = Status.RUNNING;
    }

    @Override
    public void stop() {
        switchboardMonitor.monitor_stop(this);
        this.status = Status.STOPPING;
        for (ModelAddress p : routingLogic.getAllConnectedRoutees()) {
            closeAllConnections(p);
        }
        this.routingLogic.close();
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
    }

    @SuppressWarnings("unchecked")
    protected void handleCloseConnection(ModelAddress sender, ModelAddress receiver, boolean noMoreRouteToReceiver) {
        connectorMapping.getConnectionHandler(receiver).closeConnection(sender,
                                                                        receiver,
                                                                        noMoreRouteToReceiver,
                                                                        handlerScopeContext);
    }

}
