package at.irian.ankor.switching.connector;

import at.irian.ankor.switching.routing.ModelAddress;

/**
 * The ConnectorRegistry is the authority at which {@link at.irian.ankor.switching.connector.Connector connectors}
 * register their respective handlers.
 *
 * @author Manfred Geiler
 */
public interface ConnectorRegistry extends ConnectorMapping {

    void registerConnectionHandler(Class<? extends ModelAddress> receiverAddressType,
                                   ConnectionHandler<? extends ModelAddress> connectionHandler);

    void unregisterConnectionHandler(Class<? extends ModelAddress> receiverAddressType);

    void registerTransmissionHandler(Class<? extends ModelAddress> receiverAddressType,
                                     TransmissionHandler<? extends ModelAddress> transmissionHandler);

    void unregisterTransmissionHandler(Class<? extends ModelAddress> receiverAddressType);

}
