package at.irian.ankor.monitor.nop;

import at.irian.ankor.monitor.SwitchboardMonitor;
import at.irian.ankor.switching.Switchboard;
import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.routing.ModelAddress;
import at.irian.ankor.switching.routing.ModelAddressQualifier;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class NopSwitchboardMonitor implements SwitchboardMonitor {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(NopSwitchboardMonitor.class);
    @Override
    public void monitor_openConnection(Switchboard sb, ModelAddress sender, Map<String, Object> connectParameters) {

    }

    @Override
    public void monitor_send(Switchboard sb, ModelAddress sender, EventMessage message) {

    }

    @Override
    public void monitor_send(Switchboard sb, ModelAddress sender, EventMessage message, ModelAddress receiver) {

    }

    @Override
    public void monitor_closeAllConnections(Switchboard sb, ModelAddress sender) {

    }

    @Override
    public void monitor_closeQualifyingConnections(Switchboard sb, ModelAddressQualifier qualifier) {

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
