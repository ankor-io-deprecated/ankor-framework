package at.irian.ankor.switching.connector.websocket;

import at.irian.ankor.messaging.MessageDeserializer;
import at.irian.ankor.messaging.MessageMapper;
import at.irian.ankor.messaging.MessageMapperFactory;
import at.irian.ankor.switching.connector.Connector;
import at.irian.ankor.system.AnkorSystem;

/**
 * @author Thomas Spiegl
 */
public class WebSocketConnector implements Connector {

    private AnkorSystem ankorSystem;
    private WebSocketSessionRegistry sessionRegistry;
    private MessageMapper<String> messageMapper;

    private static WebSocketConnector INSTANCE;

    @Override
    public void init(AnkorSystem ankorSystem) {
        this.ankorSystem = ankorSystem;
        this.sessionRegistry = new WebSocketSessionRegistry();
        messageMapper = new MessageMapperFactory<String>(ankorSystem).createMessageMapper();
        INSTANCE = this;
    }

    @Override
    public void start() {
        WebSocketSender webSocketSender = new WebSocketSender(sessionRegistry, ankorSystem.getSwitchboard(), messageMapper);
        ankorSystem.getConnectorPlug().registerConnectionHandler(WebSocketModelAddress.class,
                new WebSocketConnectionHandler(webSocketSender));
        ankorSystem.getConnectorPlug().registerTransmissionHandler(WebSocketModelAddress.class,
                new WebSocketTransmissionHandler(webSocketSender));
    }

    @Override
    public void stop() {
        ankorSystem.getConnectorPlug().unregisterConnectionHandler(WebSocketModelAddress.class);
        ankorSystem.getConnectorPlug().unregisterTransmissionHandler(WebSocketModelAddress.class);
    }

    public static WebSocketConnector getInstance() {
        return INSTANCE;
    }

    public MessageMapper<String> getMessageMapper() {
        return messageMapper;
    }

    public AnkorSystem getAnkorSystem() {
        return ankorSystem;
    }

    public WebSocketSessionRegistry getSessionRegistry() {
        return sessionRegistry;
    }
}
