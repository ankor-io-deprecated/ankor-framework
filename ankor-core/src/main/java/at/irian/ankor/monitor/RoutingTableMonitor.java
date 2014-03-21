package at.irian.ankor.monitor;

import at.irian.ankor.switching.routing.ModelAddress;
import at.irian.ankor.switching.routing.RoutingTable;

/**
 * @author Manfred Geiler
 */
public interface RoutingTableMonitor {

    void monitor_connect(RoutingTable rt, ModelAddress a, ModelAddress b);
    void monitor_disconnect(RoutingTable rt, ModelAddress a, ModelAddress b);

}
