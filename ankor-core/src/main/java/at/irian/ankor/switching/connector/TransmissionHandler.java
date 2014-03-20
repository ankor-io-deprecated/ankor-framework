package at.irian.ankor.switching.connector;

import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.routing.ModelAddress;

/**
 * @author Manfred Geiler
 */
public interface TransmissionHandler<P extends ModelAddress> {

    /**
     * Actually transmit an EventMessage to the given receiver.
     * @param sender    sender of the event
     * @param receiver  receiver
     * @param message   EventMessage to transmit to the receiver
     * @param context   context for sharing custom attributes in "handler scope"
     */
    void transmitEventMessage(ModelAddress sender, P receiver, EventMessage message, HandlerScopeContext context);

}
