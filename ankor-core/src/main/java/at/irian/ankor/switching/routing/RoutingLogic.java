package at.irian.ankor.switching.routing;

import java.util.Collection;
import java.util.Map;

/**
 * A RoutingLogic is responsible for finding the appropriate message recipient
 * that is suitable for a given sender and the applied connect parameters.
 *
 * @author Manfred Geiler
 */
public interface RoutingLogic {

    /**
     * @param sender ModelAddress, that requests the new connection
     * @param connectParameters  custom (application specific) parameters
     * @return ModelAddress that accepted the connection request, or null if connection was not accepted
     */
    ModelAddress connect(ModelAddress sender, Map<String, Object> connectParameters);

    Collection<ModelAddress> getConnectedRoutees(ModelAddress sender);

    Collection<ModelAddress> getAllConnectedRoutees();

    void disconnect(ModelAddress sender, ModelAddress receiver);

    void init();

    void close();

}
