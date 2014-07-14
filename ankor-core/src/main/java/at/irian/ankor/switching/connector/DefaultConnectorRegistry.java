package at.irian.ankor.switching.connector;

import at.irian.ankor.switching.routing.ModelAddress;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of the {@link at.irian.ankor.switching.connector.ConnectorRegistry ConnectorRegistry}
 * that uses {@link ConcurrentHashMap ConcurrentHashMaps} for storage of the registered handlers.
 *
 * @author Manfred Geiler
 */
public class DefaultConnectorRegistry implements ConnectorRegistry {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultConnectorRegistry.class);

    /**
     * We assume not much more than 4 connector plugins at all.
     */
    private static final int INITIAL_CAPACITY = 4;

    private static final float LOAD_FACTOR = 0.75f;

    private final Map<Class, ConnectionHandler> connectionHandlers;
    private final Map<Class, TransmissionHandler> transmissionHandlers;

    protected DefaultConnectorRegistry(Map<Class, ConnectionHandler> connectionHandlers,
                                       Map<Class, TransmissionHandler> transmissionHandlers) {
        this.connectionHandlers = connectionHandlers;
        this.transmissionHandlers = transmissionHandlers;
    }

    public static ConnectorRegistry createForSingleThread() {
        return createForConcurrency(1);
    }

    public static ConnectorRegistry createForConcurrency(int concurrencyLevel) {
        return new DefaultConnectorRegistry(new ConcurrentHashMap<Class, ConnectionHandler>(INITIAL_CAPACITY,
                                                                                            LOAD_FACTOR,
                                                                                            concurrencyLevel),
                                            new ConcurrentHashMap<Class, TransmissionHandler>(INITIAL_CAPACITY,
                                                                                              LOAD_FACTOR,
                                                                                              concurrencyLevel)
        );
    }

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
