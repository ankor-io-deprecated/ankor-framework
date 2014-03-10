package at.irian.ankor.switching.connector;

import at.irian.ankor.switching.party.Party;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public interface ConnectionHandler<P extends Party> {

    void openConnection(Party sender, P receiver, Map<String, Object> connectParameters);

    void closeConnection(Party sender, P receiver, boolean lastRoute);

}
