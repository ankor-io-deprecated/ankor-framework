package at.irian.ankor.monitor.stats;

import at.irian.ankor.monitor.SwitchboardMonitor;
import at.irian.ankor.switching.Switchboard;
import at.irian.ankor.switching.connector.local.StatefulSessionModelAddress;
import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.routing.ModelAddress;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class StatsSwitchboardMonitor implements SwitchboardMonitor {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(StatsSwitchboardMonitor.class);

    private final SwitchboardStats stats;

    public StatsSwitchboardMonitor(SwitchboardStats stats) {
        this.stats = stats;
    }

    @Override
    public void monitor_openConnection(Switchboard sb, ModelAddress sender, Map<String, Object> connectParameters) {
        // Switchboard just received a connect request and is looking for a routee
        if (sender instanceof StatefulSessionModelAddress) {
            // connect request came from local model session
        } else {
            // connect request came from external connector
            stats.incrementInboundMessages();
        }
    }

    @Override
    public void monitor_send(Switchboard sb, ModelAddress sender, EventMessage message) {
        // Switchboard just received an EventMessage and is routing it to the right receiver
        if (sender instanceof StatefulSessionModelAddress) {
            // EventMessage came from local model session
        } else {
            // EventMessage came from external connector
            stats.incrementInboundMessages();
        }
    }

    @Override
    public void monitor_send(Switchboard sb, ModelAddress sender, EventMessage message, ModelAddress receiver) {
        // Switchboard is routing an EventMessage to a specific receiver
        if (receiver instanceof StatefulSessionModelAddress) {
            // ... to another local model
        } else {
            // ... to an external receiver
            stats.incrementOutboundMessages();
        }
    }

    @Override
    public void monitor_closeAllConnections(Switchboard sb, ModelAddress sender) {

    }

    @Override
    public void monitor_closeConnection(Switchboard sb, ModelAddress sender, ModelAddress receiver) {

    }

    @Override
    public void monitor_start(Switchboard sb) {

    }

    @Override
    public void monitor_stop(Switchboard sb) {

    }
}
