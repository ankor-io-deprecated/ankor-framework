package at.irian.ankor.monitor;

import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.routing.ModelAddress;

/**
 * @author Thomas Spiegl
 */
public interface Monitor {

    void connect(ModelAddress a, ModelAddress b);
    void disconnect(ModelAddress a, ModelAddress b);
    void send(ModelAddress sender, EventMessage message, ModelAddress receiver);
    void inboundMessage(ModelAddress sender);
    void outboundMessage(ModelAddress receiver);
    void addModelSession();
    void removeModelSession();
}