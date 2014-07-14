package at.irian.ankor.monitor;

import at.irian.ankor.switching.Switchboard;
import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.routing.ModelAddress;
import at.irian.ankor.switching.routing.ModelAddressQualifier;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public interface SwitchboardMonitor {

    void monitor_openConnection(Switchboard sb, ModelAddress sender, Map<String, Object> connectParameters);
    void monitor_send(Switchboard sb, ModelAddress sender, EventMessage message);
    void monitor_send(Switchboard sb, ModelAddress sender, EventMessage message, ModelAddress receiver);
    void monitor_closeAllConnections(Switchboard sb, ModelAddress sender);
    void monitor_closeQualifyingConnections(Switchboard sb, ModelAddressQualifier qualifier);
    void monitor_closeConnection(Switchboard sb, ModelAddress sender, ModelAddress receiver);
    void monitor_start(Switchboard sb);
    void monitor_stop(Switchboard sb);

}
