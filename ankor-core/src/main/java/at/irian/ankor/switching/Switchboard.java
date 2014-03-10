package at.irian.ankor.switching;

import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.routing.ModelAddress;

import java.util.Map;

/**
 * The Switchboard is the central point for connecting shared models and handling the message sending
 * between connected models.
 *
 * @author Manfred Geiler
 */
public interface Switchboard {

    /**
     * Find a receiver address by means of a {@link at.irian.ankor.switching.routing.RoutingLogic}, establish a connection
     * by adding a route to this receiver and inform the receiver about the "connect requestor" (i.e. the sender).
     *
     * @param sender  ModelAddress that request the connection
     * @param connectParameters  criteria for the {@link at.irian.ankor.switching.routing.RoutingLogic} to find the proper receiver
     */
    void openConnection(ModelAddress sender, Map<String, Object> connectParameters);

    /**
     * Send the give EventMessage to all currently connected receivers of the given sender.
     * @param sender   ModelAddress that sends the EventMessage
     * @param message  an EventMessage
     */
    void send(ModelAddress sender, EventMessage message);

    /**
     * Send the give EventMessage to a specific receiver.
     * @param sender   ModelAddress that sends the EventMessage
     * @param receiver ModelAddress that shall receive the EventMessage
     * @param message  an EventMessage
     */
    void send(ModelAddress sender, ModelAddress receiver, EventMessage message);

    /**
     * Close the connections to all currently connected receivers of the given sender.
     * @param sender  ModelAddress that wants to close connections
     */
    void closeAllConnections(ModelAddress sender);

    /**
     * Close the connection to a specific receiver.
     * @param sender   ModelAddress that wants to close the connection
     * @param receiver ModelAddress that shall be informed about the closing of the connection
     */
    void closeConnection(ModelAddress sender, ModelAddress receiver);


    /**
     * Start switching.
     */
    void start();

    /**
     * Disconnect all addresses and stop switching.
     */
    void stop();
}
