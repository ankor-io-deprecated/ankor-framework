package at.irian.ankor.switching.connector;

import at.irian.ankor.switching.routing.ModelAddress;

/**
 * The ConnectorMapping is the abstraction for getting the right handler for a specific ModelAddress type.
 *
 * @author Manfred Geiler
 */
public interface ConnectorMapping {

    /**
     * Return the appropriate {@link ConnectionHandler} for the given {@link ModelAddress} type.
     */
    ConnectionHandler getConnectionHandler(ModelAddress receiver);

    /**
     * Return the appropriate {@link TransmissionHandler} for the given {@link ModelAddress} type.
     */
    TransmissionHandler getTransmissionHandler(ModelAddress receiver);

}
