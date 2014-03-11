package at.irian.ankor.switching.connector;

import at.irian.ankor.switching.routing.ModelAddress;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class ConcurrentConnectorRegistry implements ConnectorRegistry, ConnectorMapping {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ConcurrentConnectorRegistry.class);

    private ImmutableMap<Class, ConnectionHandler> connectionHandlers = ImmutableMap.of();
    private ImmutableMap<Class, TransmissionHandler> transmissionHandlers = ImmutableMap.of();

    @Override
    public void registerConnectionHandler(Class<? extends ModelAddress> receiverAddressType,
                                          ConnectionHandler<? extends ModelAddress> connectionHandler) {
        if (connectionHandlers.containsKey(receiverAddressType)) {
            throw new IllegalStateException("ConnectionHandler for address type " + receiverAddressType.getName() + " already registered");
        }
        connectionHandlers = new ImmutableMap.Builder<Class, ConnectionHandler>()
                .putAll(connectionHandlers)
                .put(receiverAddressType, connectionHandler)
                .build();
    }

    @Override
    public void registerTransmissionHandler(Class<? extends ModelAddress> receiverAddressType,
                                            TransmissionHandler<? extends ModelAddress> transmissionHandler) {
        if (transmissionHandlers.containsKey(receiverAddressType)) {
            throw new IllegalStateException("TransmissionHandler for address type " + receiverAddressType.getName() + " already registered");
        }
        transmissionHandlers = new ImmutableMap.Builder<Class, TransmissionHandler>()
                .putAll(transmissionHandlers)
                .put(receiverAddressType, transmissionHandler)
                .build();
    }

    @Override
    public void unregisterConnectionHandler(Class<? extends ModelAddress> receiverAddressType) {
        Map<Class, ConnectionHandler> mutableMap = new HashMap<Class, ConnectionHandler>(this.connectionHandlers);
        if (mutableMap.remove(receiverAddressType) == null) {
            LOG.warn("ConnectionHandler for address type " + receiverAddressType.getName() + " was not registered");
        } else {
            this.connectionHandlers = ImmutableMap.copyOf(mutableMap);
        }
    }

    @Override
    public void unregisterTransmissionHandler(Class<? extends ModelAddress> receiverAddressType) {
        Map<Class, TransmissionHandler> mutableMap = new HashMap<Class, TransmissionHandler>(this.transmissionHandlers);
        if (mutableMap.remove(receiverAddressType) == null) {
            LOG.warn("TransmissionHandler for address type " + receiverAddressType.getName() + " was not registered");
        } else {
            this.transmissionHandlers = ImmutableMap.copyOf(mutableMap);
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
