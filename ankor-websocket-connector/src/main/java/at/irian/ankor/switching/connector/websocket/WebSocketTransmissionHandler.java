package at.irian.ankor.switching.connector.websocket;

import at.irian.ankor.switching.connector.TransmissionHandler;
import at.irian.ankor.switching.msg.ActionEventMessage;
import at.irian.ankor.switching.msg.ChangeEventMessage;
import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.routing.ModelAddress;

/**
 * @author Thomas Spiegl
 */
public class WebSocketTransmissionHandler implements TransmissionHandler<WebSocketModelAddress> {

    //private static Logger LOG = LoggerFactory.getLogger(WebSocketTransmissionHandler.class);

    private final WebSocketSender webSocketSender;

    public WebSocketTransmissionHandler(WebSocketSender webSocketSender) {
        this.webSocketSender = webSocketSender;
    }

    @Override
    public void transmitEventMessage(ModelAddress sender, WebSocketModelAddress receiver, EventMessage message) {

        WebSocketMessage socketMessage;

        if (message instanceof ActionEventMessage) {
            socketMessage = WebSocketMessage.createActionMsg(receiver.getRemoteSystemId(),
                    ((ActionEventMessage) message).getProperty(),
                    ((ActionEventMessage) message).getAction());
        } else if (message instanceof ChangeEventMessage) {
            socketMessage = WebSocketMessage.createChangeMsg(receiver.getRemoteSystemId(),
                    ((ChangeEventMessage) message).getProperty(),
                    ((ChangeEventMessage) message).getChange());
        } else {
            throw new IllegalArgumentException("Unsupported message type " + message.getClass().getName());
        }

        webSocketSender.send(sender, receiver, socketMessage);
    }

}
