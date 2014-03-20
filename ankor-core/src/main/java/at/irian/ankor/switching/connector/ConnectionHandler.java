package at.irian.ankor.switching.connector;

import at.irian.ankor.switching.routing.ModelAddress;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public interface ConnectionHandler<P extends ModelAddress> {

    /**
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
     * @param sender     address of party that initiated the closing of the connection
     * @param receiver   address of party that shall be closed
     * @param lastRoute  true, if this is the last route to the given receiver
     * @param context    context for sharing custom attributes in "handler scope"
     */
    void closeConnection(ModelAddress sender, P receiver, boolean lastRoute, HandlerScopeContext context);

}
