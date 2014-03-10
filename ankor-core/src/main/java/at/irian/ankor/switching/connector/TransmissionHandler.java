package at.irian.ankor.switching.connector;

import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.party.Party;

/**
 * @author Manfred Geiler
 */
public interface TransmissionHandler<P extends Party> {

    void transmitEventMessage(Party sender, P receiver, EventMessage message);

}
