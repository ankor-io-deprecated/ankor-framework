package at.irian.ankor.switching.routing;

import java.util.Collection;

/**
 * @author Manfred Geiler
 */
public interface RoutingTable {

    /**
     * @param a  ModelAddress 1
     * @param b  ModelAddress 2
     * @return true, if addresses where not connected yet and got successfully connected
     */
    boolean connect(ModelAddress a, ModelAddress b);

    /**
     * @param a  ModelAddress 1
     * @param b  ModelAddress 2
     * @return true, if addresses where connected and got successfully disconnected
     */
    boolean disconnect(ModelAddress a, ModelAddress b);

    /**
     * @param a  ModelAddress 1
     * @param b  ModelAddress 2
     * @return true, if addresses are currently connected
     */
    boolean isConnected(ModelAddress a, ModelAddress b);

    /**
     * @param modelAddress  ModelAddress
     * @return true, if given modelAddress was connected to any other modelAddress and got successfully disconnected
     */
    @SuppressWarnings("UnusedDeclaration")
    boolean disconnectAll(ModelAddress modelAddress);

    /**
     * @param modelAddress  ModelAddress
     * @return collection of all other addresses the given modelAddress is currently connected to,
     *         or empty collection if there are no connected addresses
     */
    Collection<ModelAddress> getConnectedAddresses(ModelAddress modelAddress);

    /**
     * @param modelAddress  ModelAddress
     * @return true, if given modelAddress is currently connected to any other modelAddress
     */
    boolean hasConnectedAddresses(ModelAddress modelAddress);

    /**
     * @return all addresses that are currently connected
     */
    Collection<ModelAddress> getAllConnectedAddresses();

    /**
     * Completely clear the routing table.
     */
    void clear();
}
