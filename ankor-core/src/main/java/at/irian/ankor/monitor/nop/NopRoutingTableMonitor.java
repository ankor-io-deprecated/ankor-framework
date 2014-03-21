package at.irian.ankor.monitor.nop;

import at.irian.ankor.monitor.RoutingTableMonitor;
import at.irian.ankor.switching.routing.ModelAddress;
import at.irian.ankor.switching.routing.RoutingTable;

/**
 * @author Manfred Geiler
 */
public class NopRoutingTableMonitor implements RoutingTableMonitor {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(NopRoutingTableMonitor.class);

    @Override
    public void monitor_connect(RoutingTable rt, ModelAddress a, ModelAddress b) {

    }

    @Override
    public void monitor_disconnect(RoutingTable rt, ModelAddress a, ModelAddress b) {

    }
}
