package at.irian.ankor.switching.connector.websocket;

import at.irian.ankor.messaging.MessageSerializer;
import at.irian.ankor.switching.Switchboard;
import at.irian.ankor.switching.routing.ModelAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.io.IOException;

/**
 * @author Thomas Spiegl
 */
public class WebSocketSender {

    private static Logger LOG = LoggerFactory.getLogger(WebSocketSender.class);

    private final WebSocketSessionRegistry sessionRegistry;
    private final Switchboard switchboard;
    private final MessageSerializer<String> messageSerializer;

    public WebSocketSender(WebSocketSessionRegistry sessionRegistry, Switchboard switchboard, MessageSerializer<String> messageSerializer) {
        this.sessionRegistry = sessionRegistry;
        this.switchboard = switchboard;
        this.messageSerializer = messageSerializer;
    }

    public void send(ModelAddress sender, WebSocketModelAddress receiver, WebSocketMessage webSocketMessage) {
        Session session = sessionRegistry.getSession(receiver.getRemoteSystemId());
        if (session == null) {
            LOG.error("Error sending {} from {} to {} - automatically disconnecting {}, reason: NO WS SESSION FOUND ...",
                    webSocketMessage,
                    sender,
                    receiver,
                    receiver);
            switchboard.closeConnection(sender, receiver);
        } else {
            try {
                String serializedMsg = messageSerializer.serialize(webSocketMessage);
                LOG.debug("Sending serialized message to {}: {}", receiver, serializedMsg);
                session.getBasicRemote().sendText(serializedMsg);
            } catch (IOException e) {
                LOG.error("Error sending {} from {} to {} - automatically disconnecting {} ...",
                        webSocketMessage,
                        sender,
                        receiver,
                        receiver, e);
            }
        }
    }


}
