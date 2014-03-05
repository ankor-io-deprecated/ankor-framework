package at.irian.ankor.msg;

import at.irian.ankor.msg.party.Party;

import java.util.Collection;

/**
 * @author Manfred Geiler
 */
public interface RoutingTable {

    /**
     * @param a  Party 1
     * @param b  Party 2
     * @return true, if parties where not connected yet and got successfully connected
     */
    boolean connect(Party a, Party b);

    /**
     * @param a  Party 1
     * @param b  Party 2
     * @return true, if parties where connected and got successfully disconnected
     */
    boolean disconnect(Party a, Party b);

    /**
     * @param a  Party 1
     * @param b  Party 2
     * @return true, if parties are currently connected
     */
    boolean isConnected(Party a, Party b);

    /**
     * @param party  Party
     * @return true, if given party was connected to any other party and got successfully disconnected
     */
    boolean disconnectAll(Party party);

    /**
     * @param party  Party
     * @return collection of all other parties the given party is currently connected to
     */
    Collection<Party> getConnectedParties(Party party);

    /**
     * @param party  Party
     * @return true, if given party is currently connected to any other party
     */
    boolean hasConnectedParties(Party party);
}
