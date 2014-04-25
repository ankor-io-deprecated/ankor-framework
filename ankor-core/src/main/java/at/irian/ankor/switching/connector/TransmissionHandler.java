package at.irian.ankor.switching.connector;

import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.routing.ModelAddress;

/**
 * {@link Connector}-specific handler for transmitting event messages from one model instance to another.
 *
 * @author Manfred Geiler
 */
public interface TransmissionHandler<P extends ModelAddress> {

    /**
     * Actually transmit an EventMessage to the given {@link Connector}-specific receiver.
     * @param sender    sender of the event
     * @param receiver  receiver
     * @param message   EventMessage to transmit to the receiver
     * @param context   context for sharing custom attributes in "handler scope"
     */
    void transmitEventMessage(ModelAddress sender, P receiver, EventMessage message, HandlerScopeContext context);

}
