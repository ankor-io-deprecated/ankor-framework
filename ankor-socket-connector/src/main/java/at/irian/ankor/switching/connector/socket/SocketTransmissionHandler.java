package at.irian.ankor.switching.connector.socket;

import at.irian.ankor.serialization.MessageSerializer;
import at.irian.ankor.switching.Switchboard;
import at.irian.ankor.switching.connector.HandlerScopeContext;
import at.irian.ankor.switching.connector.TransmissionHandler;
import at.irian.ankor.switching.msg.ActionEventMessage;
import at.irian.ankor.switching.msg.ChangeEventMessage;
import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.routing.ModelAddress;

import java.io.IOException;
import java.net.URI;

/**
 * @author Manfred Geiler
 */
public class SocketTransmissionHandler implements TransmissionHandler<SocketModelAddress> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketTransmissionHandler.class);

    private final URI localAddress;
    private final MessageSerializer<String> messageSerializer;
    private final Switchboard switchboard;

    public SocketTransmissionHandler(URI localAddress,
                                     MessageSerializer<String> messageSerializer,
                                     Switchboard switchboard) {
        this.localAddress = localAddress;
        this.messageSerializer = messageSerializer;
        this.switchboard = switchboard;
    }

    @Override
    public void transmitEventMessage(ModelAddress sender,
                                     SocketModelAddress receiver,
                                     EventMessage message,
                                     HandlerScopeContext context) {
        try {
            send(receiver, message);
        } catch (IOException e) {
            LOG.error("Error sending {} from {} to {} - automatically disconnecting {} ...",
                      message,
                      sender,
                      receiver,
                      receiver);
            switchboard.closeConnection(sender, receiver);
        }
    }

    private void send(SocketModelAddress receiver, EventMessage message) throws IOException {
        SocketMessage socketMessage;

        if (message instanceof ActionEventMessage) {
            socketMessage = SocketMessage.createActionMsg(localAddress.toString(),
                                                          ((ActionEventMessage) message).getProperty(),
                                                          ((ActionEventMessage) message).getAction(),
                                                          ((ActionEventMessage) message).getStateValues(),
                                                          ((ActionEventMessage) message).getStateHolderProperties());
        } else if (message instanceof ChangeEventMessage) {
            socketMessage = SocketMessage.createChangeMsg(localAddress.toString(),
                                                          ((ChangeEventMessage) message).getProperty(),
                                                          ((ChangeEventMessage) message).getChange(),
                                                          ((ChangeEventMessage) message).getStateValues(),
                                                          ((ChangeEventMessage) message).getStateHolderProperties());
        } else {
            throw new IllegalArgumentException("Unsupported message type " + message.getClass().getName());
        }

        sendSocketMessage(receiver, socketMessage);
    }

    private void sendSocketMessage(SocketModelAddress receiver, SocketMessage socketMessage) throws IOException {
        new SocketSender(messageSerializer).send(receiver, socketMessage);
    }

}
