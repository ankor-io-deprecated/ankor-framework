package at.irian.ankor.switching;

import at.irian.ankor.switching.connector.ConnectorRegistry;
import at.irian.ankor.switching.connector.DefaultConnectorRegistry;
import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.routing.ConcurrentRoutingTable;
import at.irian.ankor.switching.routing.ModelAddress;
import at.irian.ankor.switching.routing.RoutingLogic;
import at.irian.ankor.switching.routing.RoutingTable;

/**
 * @author Manfred Geiler
 */
public class DefaultSwitchboard extends AbstractSwitchboard implements SwitchboardImplementor {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultSwitchboard.class);

    private static final int DEFAULT_CONCURRENCY_LEVEL = 16;

    private final ConnectorRegistry connectorRegistry;

    protected DefaultSwitchboard(RoutingTable routingTable, ConnectorRegistry connectorRegistry) {
        super(routingTable, connectorRegistry);
        this.connectorRegistry = connectorRegistry;
    }

    public static SwitchboardImplementor createForSingleThread() {
        ConnectorRegistry connectorRegistry = DefaultConnectorRegistry.createForSingleThread();
        return new DefaultSwitchboard(new ConcurrentRoutingTable(), connectorRegistry);
    }

    public static SwitchboardImplementor createForConcurrency() {
        return createForConcurrency(DEFAULT_CONCURRENCY_LEVEL);
    }

    public static SwitchboardImplementor createForConcurrency(int concurrencyLevel) {
        ConnectorRegistry connectorRegistry = DefaultConnectorRegistry.createForConcurrency(concurrencyLevel);
        return new DefaultSwitchboard(new ConcurrentRoutingTable(), connectorRegistry);
    }

    @Override
    public void setRoutingLogic(RoutingLogic routingLogic) {
        super.setRoutingLogic(routingLogic);
    }

    @Override
    public ConnectorRegistry getConnectorRegistry() {
        return connectorRegistry;
    }

    @Override
    protected void dispatchableSend(ModelAddress originalSender, ModelAddress receiver, EventMessage message) {
        send(originalSender, message, receiver);
    }

    @Override
    protected void dispatchableCloseConnection(ModelAddress sender, ModelAddress receiver) {
        closeConnection(sender, receiver);
    }

}
