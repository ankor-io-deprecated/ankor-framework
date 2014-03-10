package at.irian.ankor.switching.routing;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public interface RoutingLogic {

    /**
     * @param sender ModelAddress, that requests the new connection
     * @param connectParameters  custom (application specific) parameters
     * @return ModelAddress that accepted the connection request, or null if connection was not accepted
     */
    ModelAddress findRoutee(ModelAddress sender, Map<String, Object> connectParameters);

}
