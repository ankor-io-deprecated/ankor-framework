package at.irian.ankor.switching;

import at.irian.ankor.switching.connector.ConnectorPlug;
import at.irian.ankor.switching.routing.RoutingLogic;

/**
 * @author Manfred Geiler
 */
public interface PluggableSwitchboard extends Switchboard {

    void setRoutingLogic(RoutingLogic routingLogic);

    ConnectorPlug getConnectorPlug();
}
