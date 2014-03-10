package at.irian.ankor.switching.routing;

import at.irian.ankor.switching.party.Party;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public interface RoutingLogic {

    /**
     * @param sender Party, that requests the new connection
     * @param connectParameters  custom (application specific) parameters
     * @return Party that accepted the connection request, or null if connection was not accepted
     */
    Party findRoutee(Party sender, Map<String, Object> connectParameters);

}
