package at.irian.ankor.gateway.handler;

import at.irian.ankor.gateway.party.Party;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public interface ConnectHandler {

    /**
     * @param sender Party, that requests the new connection
     * @param connectParameters  custom (application specific) parameters
     * @return Party that accepted the connection request, or null if connection was not accepted
     */
    Party findReceiver(Party sender, Map<String, Object> connectParameters);

}
