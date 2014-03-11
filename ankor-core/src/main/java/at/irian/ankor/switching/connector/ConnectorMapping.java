package at.irian.ankor.switching.connector;

import at.irian.ankor.switching.routing.ModelAddress;

/**
 * @author Manfred Geiler
 */
public interface ConnectorMapping {

    ConnectionHandler getConnectionHandler(ModelAddress receiver);

    TransmissionHandler getTransmissionHandler(ModelAddress receiver);

}
