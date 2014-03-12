package at.irian.ankor.switching;

import at.irian.ankor.switching.connector.ConcurrentConnectorRegistry;
import at.irian.ankor.switching.connector.ConnectorMapping;
import at.irian.ankor.switching.connector.ConnectorRegistry;
import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.routing.*;

/**
 * @author Manfred Geiler
 */
public class ConcurrentSwitchboard extends AbstractSwitchboard implements SwitchboardImplementor {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ConcurrentSwitchboard.class);

    private final ConnectorRegistry connectorRegistry;

    protected ConcurrentSwitchboard(RoutingTable routingTable,
                                    ConnectorMapping connectorMapping,
                                    ConnectorRegistry connectorRegistry) {
        super(routingTable, connectorMapping);
        this.connectorRegistry = connectorRegistry;
    }

    public static SwitchboardImplementor create() {
        ConcurrentConnectorRegistry connectorRegistry = new ConcurrentConnectorRegistry();
        return new ConcurrentSwitchboard(new ConcurrentRoutingTable(),
                                         connectorRegistry,
                                         connectorRegistry);
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
