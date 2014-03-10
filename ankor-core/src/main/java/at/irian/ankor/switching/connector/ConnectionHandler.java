package at.irian.ankor.switching.connector;

import at.irian.ankor.switching.routing.ModelAddress;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public interface ConnectionHandler<P extends ModelAddress> {

    void openConnection(ModelAddress sender, P receiver, Map<String, Object> connectParameters);

    void closeConnection(ModelAddress sender, P receiver, boolean lastRoute);

}
