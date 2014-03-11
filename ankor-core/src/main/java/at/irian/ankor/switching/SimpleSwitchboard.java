package at.irian.ankor.switching;

import at.irian.ankor.switching.connector.ConnectorMapping;
import at.irian.ankor.switching.connector.ConnectorRegistry;
import at.irian.ankor.switching.connector.SimpleConnectorRegistry;
import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.routing.ModelAddress;
import at.irian.ankor.switching.routing.RoutingLogic;
import at.irian.ankor.switching.routing.RoutingTable;
import at.irian.ankor.switching.routing.SimpleRoutingTable;

/**
 * @author Manfred Geiler
 */
public class SimpleSwitchboard extends AbstractSwitchboard implements SwitchboardImplementor {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleSwitchboard.class);

    private final ConnectorRegistry connectorRegistry;

    protected SimpleSwitchboard(RoutingTable routingTable,
                                ConnectorMapping connectorMapping,
                                ConnectorRegistry connectorRegistry) {
        super(routingTable, connectorMapping);
        this.connectorRegistry = connectorRegistry;
    }

    public static SwitchboardImplementor create() {
        SimpleConnectorRegistry connectorRegistry = new SimpleConnectorRegistry();
        return new SimpleSwitchboard(new SimpleRoutingTable(), connectorRegistry, connectorRegistry);
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
        send(originalSender, receiver, message);
    }

    @Override
    protected void dispatchableCloseConnection(ModelAddress sender, ModelAddress receiver) {
        closeConnection(sender, receiver);
    }
}
