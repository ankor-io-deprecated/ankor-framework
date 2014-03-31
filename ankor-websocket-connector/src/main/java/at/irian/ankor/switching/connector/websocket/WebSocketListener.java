package at.irian.ankor.switching.connector.websocket;

import at.irian.ankor.action.Action;
import at.irian.ankor.change.Change;
import at.irian.ankor.serialization.MessageDeserializer;
import at.irian.ankor.path.PathSyntax;
import at.irian.ankor.switching.Switchboard;
import at.irian.ankor.switching.msg.ActionEventMessage;
import at.irian.ankor.switching.msg.ChangeEventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.MessageHandler;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * @author Thomas Spiegl
 */
public class WebSocketListener {

    private static Logger LOG = LoggerFactory.getLogger(WebSocketListener.class);

    private final String path;
    private final String clientId;
    private final MessageDeserializer<String> messageDeserializer;
    private final Switchboard switchboard;
    private final PathSyntax pathSyntax;

    WebSocketListener(String path,
                      MessageDeserializer<String> messageDeserializer,
                      Switchboard switchboard,
                      PathSyntax pathSyntax,
                      String clientId) {
        this.path = path;
        this.messageDeserializer = messageDeserializer;
        this.switchboard = switchboard;
        this.pathSyntax = pathSyntax;
        this.clientId = clientId;
    }

    public void onWebSocketMessage(String message) {
        if (!isHeartbeat(message)) {
            LOG.info("MessageHandler received {}, length = {}", message, message.length());
            try {
                WebSocketMessage socketMessage = messageDeserializer.deserialize(message, WebSocketMessage.class);
                if (socketMessage.getAction() != null) {
                    handleIncomingActionEventMessage(socketMessage);
                } else if (socketMessage.getChange() != null) {
                    handleIncomingChangeEventMessage(socketMessage);
                } else if (socketMessage.isClose()) {
                    handleIncomingCloseMessage(socketMessage);
                } else {
                    handleIncomingConnectMessage(socketMessage);
                }
            } catch (Exception e) {
                LOG.error("Exception while handling socket message " + message, e);
            }

        }
    }

    private void handleIncomingConnectMessage(WebSocketMessage socketMessage) {
        WebSocketModelAddress sender = getAddress(socketMessage);
        switchboard.openConnection(sender, socketMessage.getConnectParams());
    }

    private void handleIncomingActionEventMessage(WebSocketMessage socketMessage) {
        Action action = socketMessage.getAction();
        WebSocketModelAddress sender = getAddress(socketMessage);
        switchboard.send(sender, new ActionEventMessage(socketMessage.getProperty(), action));
    }

    private void handleIncomingChangeEventMessage(WebSocketMessage socketMessage) {
        Change change = socketMessage.getChange();
        WebSocketModelAddress sender = getAddress(socketMessage);
        switchboard.send(sender, new ChangeEventMessage(socketMessage.getProperty(), change));
    }

    private void handleIncomingCloseMessage(WebSocketMessage socketMessage) {
        WebSocketModelAddress sender = getAddress(socketMessage);
        switchboard.closeAllConnections(sender);
    }

    private WebSocketModelAddress getAddress(WebSocketMessage socketMessage) {
        String modelName = pathSyntax.rootOf(socketMessage.getProperty());
        return new WebSocketModelAddress(path, clientId, modelName);
    }

    public static boolean isHeartbeat(String message) {
        return message.trim().equals("");
    }


    public  MessageHandler.Whole<ByteBuffer> getByteMessageHandler() {
        return new MessageHandler.Whole<ByteBuffer>() {
            @Override
            public void onMessage(ByteBuffer message) {
                onWebSocketMessage(new String(message.array(), Charset.forName("UTF-8")));
            }
        };
    }

    public MessageHandler.Whole<String> getStringMessageHandler() {
        return new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                onWebSocketMessage(message);
            }
        };
    }


}