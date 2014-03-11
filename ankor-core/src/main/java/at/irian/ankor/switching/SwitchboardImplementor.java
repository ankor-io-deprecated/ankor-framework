package at.irian.ankor.switching;

import at.irian.ankor.switching.connector.ConnectorRegistry;
import at.irian.ankor.switching.routing.RoutingLogic;

/**
 * @author Manfred Geiler
 */
public interface SwitchboardImplementor extends Switchboard {

    ConnectorRegistry getConnectorRegistry();

    void setRoutingLogic(RoutingLogic routingLogic);
}
