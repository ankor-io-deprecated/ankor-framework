package at.irian.ankor.switching.connector;

import at.irian.ankor.switching.routing.ModelAddress;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class SimpleConnectorRegistry implements ConnectorRegistry, ConnectorMapping {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleConnectorRegistry.class);

    private final Map<Class<? extends ModelAddress>, ConnectionHandler<? extends ModelAddress>> connectionHandlers
            = new HashMap<Class<? extends ModelAddress>, ConnectionHandler<? extends ModelAddress>>();
    private final Map<Class<? extends ModelAddress>, TransmissionHandler<? extends ModelAddress>> transmissionHandlers
            = new HashMap<Class<? extends ModelAddress>, TransmissionHandler<? extends ModelAddress>>();

    @Override
    public void registerConnectionHandler(Class<? extends ModelAddress> receiverAddressType,
                                          ConnectionHandler<? extends ModelAddress> connectionHandler) {
        if (connectionHandlers.put(receiverAddressType, connectionHandler) != null) {
            throw new IllegalStateException("ConnectionHandler for address type " + receiverAddressType.getName() + " already registered");
        }
    }

    @Override
    public void registerTransmissionHandler(Class<? extends ModelAddress> receiverAddressType,
                                            TransmissionHandler<? extends ModelAddress> transmissionHandler) {
        if (transmissionHandlers.put(receiverAddressType, transmissionHandler) != null) {
            throw new IllegalStateException("TransmissionHandler for address type " + receiverAddressType.getName() + " already registered");
        }
    }

    @Override
    public void unregisterConnectionHandler(Class<? extends ModelAddress> receiverAddressType) {
        if (connectionHandlers.remove(receiverAddressType) == null) {
            LOG.warn("ConnectionHandler for address type " + receiverAddressType.getName() + " was not registered");
        }
    }

    @Override
    public void unregisterTransmissionHandler(Class<? extends ModelAddress> receiverAddressType) {
        if (transmissionHandlers.remove(receiverAddressType) == null) {
            LOG.warn("TransmissionHandler for address type " + receiverAddressType.getName() + " was not registered");
        }
    }

    @Override
    public ConnectionHandler getConnectionHandler(ModelAddress receiver) {
        Class<? extends ModelAddress> addressType = receiver.getClass();
        ConnectionHandler connectionHandler = connectionHandlers.get(addressType);
        if (connectionHandler == null) {
            throw new IllegalStateException("No ConnectionHandler found for address type " + addressType);
        }
        return connectionHandler;
    }

    @Override
    public TransmissionHandler getTransmissionHandler(ModelAddress receiver) {
        Class<? extends ModelAddress> addressType = receiver.getClass();
        TransmissionHandler transmissionHandler = transmissionHandlers.get(addressType);
        if (transmissionHandler == null) {
            throw new IllegalStateException("No TransmissionHandler found for address type " + addressType);
        }
        return transmissionHandler;
    }

}
