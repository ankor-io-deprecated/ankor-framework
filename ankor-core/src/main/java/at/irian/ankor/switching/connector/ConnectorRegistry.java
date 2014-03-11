package at.irian.ankor.switching.connector;

import at.irian.ankor.switching.routing.ModelAddress;

/**
 * @author Manfred Geiler
 */
public interface ConnectorRegistry {

    void registerConnectionHandler(Class<? extends ModelAddress> receiverAddressType,
                                   ConnectionHandler<? extends ModelAddress> connectionHandler);

    void unregisterConnectionHandler(Class<? extends ModelAddress> receiverAddressType);

    void registerTransmissionHandler(Class<? extends ModelAddress> receiverAddressType,
                                     TransmissionHandler<? extends ModelAddress> transmissionHandler);

    void unregisterTransmissionHandler(Class<? extends ModelAddress> receiverAddressType);

}
