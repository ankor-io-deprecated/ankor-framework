package at.irian.ankor.switching;

import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.party.Party;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public interface Switchboard {

    void openConnection(Party sender, Map<String, Object> connectParameters);


    void send(Party sender, EventMessage message);

    void send(Party sender, Party receiver, EventMessage message);


    void closeAllConnections(Party sender);

    void closeConnection(Party sender, Party receiver);


    void start();

    void stop();
}
