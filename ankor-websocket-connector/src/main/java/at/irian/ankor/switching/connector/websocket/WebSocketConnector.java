package at.irian.ankor.switching.connector.websocket;

import at.irian.ankor.serialization.MessageDeserializer;
import at.irian.ankor.serialization.MessageMapperFactory;
import at.irian.ankor.switching.connector.Connector;
import at.irian.ankor.system.AnkorSystem;

/**
 * @author Thomas Spiegl
 */
public class WebSocketConnector implements Connector {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(WebSocketConnector.class);

    private static final String MESSAGE_MAPPER_ATTR_KEY = WebSocketConnector.class + ".MessageMapper";
    private static final String MESSAGE_DESERIALIZER_ATTR_KEY = WebSocketConnector.class + ".MessageDeserializer";
    private static final String WEB_SOCKET_SESSION_REGISTRY_ATTR_KEY = WebSocketConnector.class + ".WebSocketSessionRegistry";

    private boolean enabled;
    private AnkorSystem ankorSystem;

    @Override
    public void init(AnkorSystem ankorSystem) {
        this.enabled = ankorSystem.getConfig().getBoolean("at.irian.ankor.switching.connector.websocket.WebSocketConnector.enabled");
        if (!enabled) {
            LOG.info("WebSocketConnector not initialized because it is disabled - see ./ankor-websocket-connector/src/main/resources/reference.conf for details on how to enable websocket support");
            return;
        }

        this.ankorSystem = ankorSystem;
        MessageMapperFactory<String> messageMapperFactory = new MessageMapperFactory<String>(ankorSystem);
        registerMessageMapper(ankorSystem, messageMapperFactory);
        registerSingletonMessageDeserializer(ankorSystem, messageMapperFactory);
        registerSessionRegistry(ankorSystem, new WebSocketSessionRegistry());
        LOG.info("WebSocketConnector initialized");
    }

    @Override
    public void start() {
        if (!enabled) {
            LOG.debug("WebSocketConnector not started because it is disabled");
            return;
        }

        WebSocketSessionRegistry sessionRegistry = getSessionRegistry(ankorSystem);
        WebSocketSender webSocketSender = new WebSocketSender(sessionRegistry, ankorSystem.getSwitchboard(),
                                                              getMessageMapperFactory(ankorSystem));
        ankorSystem.getConnectorPlug().registerConnectionHandler(WebSocketModelAddress.class,
                                                                 new WebSocketConnectionHandler(webSocketSender));
        ankorSystem.getConnectorPlug().registerTransmissionHandler(WebSocketModelAddress.class,
                                                                   new WebSocketTransmissionHandler(webSocketSender));
    }

    @Override
    public void stop() {
        if (!enabled) {
            return;
        }

        ankorSystem.getConnectorPlug().unregisterConnectionHandler(WebSocketModelAddress.class);
        ankorSystem.getConnectorPlug().unregisterTransmissionHandler(WebSocketModelAddress.class);
    }

    private void registerMessageMapper(AnkorSystem ankorSystem, MessageMapperFactory<String> messageMapperFactory) {
        ankorSystem.getAttributes().put(MESSAGE_MAPPER_ATTR_KEY, messageMapperFactory);
    }

    public static MessageMapperFactory<String> getMessageMapperFactory(AnkorSystem ankorSystem) {
        @SuppressWarnings("unchecked") MessageMapperFactory<String> messageMapperFactory
                = (MessageMapperFactory) ankorSystem.getAttributes().get(MESSAGE_MAPPER_ATTR_KEY);
        if (messageMapperFactory == null) {
            throw new IllegalStateException("WebSocketConnector not initialized for " + ankorSystem);
        }
        return messageMapperFactory;
    }

    private void registerSingletonMessageDeserializer(AnkorSystem ankorSystem,
                                                      MessageMapperFactory<String> messageMapperFactory) {
        ankorSystem.getAttributes().put(MESSAGE_DESERIALIZER_ATTR_KEY, messageMapperFactory.createMessageMapper());
    }

    public static MessageDeserializer<String> getSingletonMessageDeserializer(AnkorSystem ankorSystem) {
        @SuppressWarnings("unchecked") MessageDeserializer<String> messageDeserializer
                = (MessageDeserializer) ankorSystem.getAttributes().get(MESSAGE_DESERIALIZER_ATTR_KEY);
        if (messageDeserializer == null) {
            throw new IllegalStateException("WebSocketConnector not initialized for " + ankorSystem);
        }
        return messageDeserializer;
    }

    private void registerSessionRegistry(AnkorSystem ankorSystem, WebSocketSessionRegistry sessionRegistry) {
        ankorSystem.getAttributes().put(WEB_SOCKET_SESSION_REGISTRY_ATTR_KEY, sessionRegistry);
    }

    public static WebSocketSessionRegistry getSessionRegistry(AnkorSystem ankorSystem) {
        WebSocketSessionRegistry sessionRegistry
                = (WebSocketSessionRegistry) ankorSystem.getAttributes().get(WEB_SOCKET_SESSION_REGISTRY_ATTR_KEY);
        if (sessionRegistry == null) {
            throw new IllegalStateException("WebSocketConnector not initialized for " + ankorSystem);
        }
        return sessionRegistry;
    }
}
