package at.irian.ankor.switching.connector.websocket;

import at.irian.ankor.serialization.MessageMapperFactory;
import at.irian.ankor.serialization.MessageSerializer;
import at.irian.ankor.switching.Switchboard;
import at.irian.ankor.switching.connector.HandlerScopeContext;
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

    private static final String MESSAGE_SERIALIZER_CTXT_ATTR_KEY = WebSocketSender.class.getName() + ".MessageSerializer";

    private final WebSocketSessionRegistry sessionRegistry;
    private final Switchboard switchboard;
    private final MessageMapperFactory<String> messageMapperFactory;

    public WebSocketSender(WebSocketSessionRegistry sessionRegistry,
                           Switchboard switchboard,
                           MessageMapperFactory<String> messageMapperFactory) {
        this.sessionRegistry = sessionRegistry;
        this.switchboard = switchboard;
        this.messageMapperFactory = messageMapperFactory;
    }

    public void send(ModelAddress sender,
                     WebSocketModelAddress receiver,
                     WebSocketMessage message,
                     HandlerScopeContext context) {
        for (Session session : sessionRegistry.getSessions(receiver.getClientId())) {
            if (!session.isOpen()) {
                if (!message.isClose()) {
                    LOG.debug("Cannot send {} from {} to {} - automatically disconnecting {}, reason: NO WS SESSION NOT OPEN ...",
                            message,
                            sender,
                            receiver,
                            receiver);
                }
                sessionRegistry.removeSession(receiver.getClientId(), session);
            } else {
                try {
                    String serializedMsg = getMessageSerializer(context).serialize(message);
                    LOG.debug("Sending serialized message to {}: {}", receiver, serializedMsg);
                    session.getBasicRemote().sendText(serializedMsg);
                } catch (IOException e) {
                    LOG.debug("Error sending {} from {} to {} - automatically disconnecting {} ...",
                            message,
                            sender,
                            receiver,
                            receiver, e);
                    sessionRegistry.removeSession(receiver.getClientId(), session);
                }
            }
        }
        if (!message.isClose()) {
            if (sessionRegistry.getSessions(receiver.getClientId()).isEmpty()) {
                switchboard.closeConnection(sender, receiver);
            }
        }
    }

    private MessageSerializer<String> getMessageSerializer(HandlerScopeContext context) {
        @SuppressWarnings("unchecked")
        MessageSerializer<String> messageSerializer
                = (MessageSerializer<String>) context.getAttributes().get(MESSAGE_SERIALIZER_CTXT_ATTR_KEY);
        if (messageSerializer == null) {
            messageSerializer = messageMapperFactory.createMessageMapper();
            context.getAttributes().put(MESSAGE_SERIALIZER_CTXT_ATTR_KEY, messageSerializer);
        }
        return messageSerializer;
    }


}
