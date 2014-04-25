package at.irian.ankor.switching.connector;

import at.irian.ankor.switching.routing.ModelAddress;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public interface ConnectionHandler<P extends ModelAddress> {

    /**
     * Handle a connect request from the given sender to the given {@link Connector}-specific receiver.
     *
     * @param sender     address of party that wants to connect
     * @param receiver   address of party that
     * @param connectParameters  application specific parameters
     * @param context    context for sharing custom attributes in "handler scope"
     */
    void openConnection(ModelAddress sender,
                        P receiver,
                        Map<String, Object> connectParameters,
                        HandlerScopeContext context);

    /**
     * Given sender tells us that it is closing the connection to the given {@link Connector}-specific receiver.
     *
     * @param sender     address of party that initiated the closing of the connection
     * @param receiver   address of party that shall be closed
     * @param lastRoute  true, if this is the last route to the given receiver
     * @param context    context for sharing custom attributes in "handler scope"
     */
    void closeConnection(ModelAddress sender, P receiver, boolean lastRoute, HandlerScopeContext context);

}
