package at.irian.ankor.switching;

import at.irian.ankor.monitor.Monitor;
import at.irian.ankor.monitor.NoMonitor;
import at.irian.ankor.switching.connector.*;
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

    protected DefaultSwitchboard(RoutingTable routingTable,
                                 ConnectorRegistry connectorRegistry,
                                 HandlerScopeContext handlerScopeContext,
                                 Monitor monitor) {
        super(routingTable, connectorRegistry, handlerScopeContext, monitor);
        this.connectorRegistry = connectorRegistry;
    }

    public static SwitchboardImplementor createForSingleThread() {
        ConnectorRegistry connectorRegistry = DefaultConnectorRegistry.createForSingleThread();
        return new DefaultSwitchboard(new ConcurrentRoutingTable(new NoMonitor()),
                                      connectorRegistry,
                                      new SimpleHandlerScopeContext(),
                                      new NoMonitor());
    }

    public static SwitchboardImplementor createForConcurrency() {
        return createForConcurrency(DEFAULT_CONCURRENCY_LEVEL);
    }

    public static SwitchboardImplementor createForConcurrency(int concurrencyLevel) {
        ConnectorRegistry connectorRegistry = DefaultConnectorRegistry.createForConcurrency(concurrencyLevel);
        return new DefaultSwitchboard(new ConcurrentRoutingTable(new NoMonitor()),
                                      connectorRegistry,
                                      new ThreadLocalHandlerScopeContext(),
                                      new NoMonitor());
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
