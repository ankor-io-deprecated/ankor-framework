package at.irian.ankor.monitor;

import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.routing.ModelAddress;

/**
 * @author Thomas Spiegl
 */
public class NoMonitor implements Monitor {

    @Override
    public void connect(ModelAddress a, ModelAddress b) {

    }

    @Override
    public void disconnect(ModelAddress a, ModelAddress b) {

    }

    @Override
    public void send(ModelAddress sender, EventMessage message, ModelAddress receiver) {

    }

    @Override
    public void inboundMessage(ModelAddress sender) {

    }

    @Override
    public void outboundMessage(ModelAddress receiver) {

    }

    @Override
    public void addModelSession() {

    }

    @Override
    public void removeModelSession() {

    }
}
