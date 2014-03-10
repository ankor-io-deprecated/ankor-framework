package at.irian.ankor.switching.connector;

import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.routing.ModelAddress;

/**
 * @author Manfred Geiler
 */
public interface TransmissionHandler<P extends ModelAddress> {

    void transmitEventMessage(ModelAddress sender, P receiver, EventMessage message);

}
