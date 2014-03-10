package at.irian.ankor.switching;

import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.party.Party;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public interface Switchboard {

    /**
     * Find a receiver party by means of a {@link at.irian.ankor.switching.routing.RoutingLogic}, establish a connection
     * by adding a route to this receiver and inform the receiver about the "connect requestor" (i.e. the sender).
     *
     * @param sender  Party that request the connection
     * @param connectParameters  criteria for the {@link at.irian.ankor.switching.routing.RoutingLogic} to find the proper receiver
     */
    void openConnection(Party sender, Map<String, Object> connectParameters);

    /**
     * Send the give EventMessage to all currently connected receivers of the given sender.
     * @param sender   Party that sends the EventMessage
     * @param message  an EventMessage
     */
    void send(Party sender, EventMessage message);

    /**
     * Send the give EventMessage to a specific receiver.
     * @param sender   Party that sends the EventMessage
     * @param receiver Party that shall receive the EventMessage
     * @param message  an EventMessage
     */
    void send(Party sender, Party receiver, EventMessage message);

    /**
     * Close the connections to all currently connected receivers of the given sender.
     * @param sender  Party that wants to close connections
     */
    void closeAllConnections(Party sender);

    /**
     * Close the connection to a specific receiver.
     * @param sender   Party that wants to close the connection
     * @param receiver Party that shall be informed about the closing of the connection
     */
    void closeConnection(Party sender, Party receiver);


    /**
     * Start switching.
     */
    void start();

    /**
     * Disconnect all parties and stop switching.
     */
    void stop();
}
