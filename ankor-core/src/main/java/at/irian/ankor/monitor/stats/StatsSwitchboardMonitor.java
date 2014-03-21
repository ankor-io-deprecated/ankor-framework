package at.irian.ankor.monitor.stats;

import at.irian.ankor.monitor.SwitchboardMonitor;
import at.irian.ankor.switching.Switchboard;
import at.irian.ankor.switching.connector.local.LocalModelAddress;
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

    }

    @Override
    public void monitor_send(Switchboard sb, ModelAddress sender, EventMessage message) {
        // Switchboard just received an EventMessage and is routing it to the right receiver
        if (sender instanceof LocalModelAddress) {
            // EventMessage came from local model session
        } else {
            // EventMessage came from external connector
            stats.incrementInboundMessages(1);
        }
    }

    @Override
    public void monitor_send(Switchboard sb, ModelAddress sender, EventMessage message, ModelAddress receiver) {
        // Switchboard is routing an EventMessage to a specific receiver
        if (receiver instanceof LocalModelAddress) {
            // ... to another local model
        } else {
            // ... to an external receiver
            stats.incrementOutboundMessages(1);
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
