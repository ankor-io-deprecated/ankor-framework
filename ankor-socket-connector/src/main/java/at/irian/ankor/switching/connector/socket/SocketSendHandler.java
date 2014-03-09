package at.irian.ankor.switching.connector.socket;

import at.irian.ankor.switching.handler.SendHandler;
import at.irian.ankor.switching.Switchboard;
import at.irian.ankor.switching.msg.ActionEventMessage;
import at.irian.ankor.switching.msg.ChangeEventMessage;
import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.party.SocketParty;
import at.irian.ankor.messaging.MessageSerializer;
import at.irian.ankor.switching.party.Party;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class SocketSendHandler implements SendHandler<SocketParty> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketSendHandler.class);

    private final URI localAddress;
    private final MessageSerializer<String> messageSerializer;
    private final Switchboard switchboard;

    public SocketSendHandler(URI localAddress,
                             MessageSerializer<String> messageSerializer,
                             Switchboard switchboard) {
        this.localAddress = localAddress;
        this.messageSerializer = messageSerializer;
        this.switchboard = switchboard;
    }

    @Override
    public void sendConnectRequest(Party sender, SocketParty receiver, Map<String, Object> connectParameters) {
        try {
            SocketMessage socketMessage = SocketMessage.createConnectMsg(localAddress.toString(),
                                                                         sender.getModelName(),
                                                                         connectParameters);
            sendSocketMessage(receiver, socketMessage);
        } catch (IOException e) {
            LOG.error("Error sending connect msg from {} to {}", sender, receiver);
        }
    }

    @Override
    public void sendEventMessage(Party sender, SocketParty receiver, EventMessage message) {
        try {
            send(receiver, message);
        } catch (IOException e) {
            LOG.error("Error sending msg {} to {} - party is getting disconnected", message, receiver);
            switchboard.close(receiver);
        }
    }

    @Override
    public void sendCloseRequest(Party sender, SocketParty receiver) {
        try {
            SocketMessage socketMessage = SocketMessage.createCloseMsg(localAddress.toString(),
                                                                       sender.getModelName());
            sendSocketMessage(receiver, socketMessage);
        } catch (IOException e) {
            LOG.error("Error sending close msg from {} to {}", sender, receiver);
        }
    }

    private void send(SocketParty receiver, EventMessage message) throws IOException {
        SocketMessage socketMessage;

        if (message instanceof ActionEventMessage) {
            socketMessage = SocketMessage.createActionMsg(localAddress.toString(),
                                                          ((ActionEventMessage) message).getProperty(),
                                                          ((ActionEventMessage) message).getAction());
        } else if (message instanceof ChangeEventMessage) {
            socketMessage = SocketMessage.createChangeMsg(localAddress.toString(),
                                                          ((ChangeEventMessage) message).getProperty(),
                                                          ((ChangeEventMessage) message).getChange());
        } else {
            throw new IllegalArgumentException("Unsupported message type " + message.getClass().getName());
        }

        sendSocketMessage(receiver, socketMessage);
    }

    private void sendSocketMessage(SocketParty receiver, SocketMessage socketMessage) throws IOException {
        String serializedMsg = messageSerializer.serialize(socketMessage);

        new SocketSender(receiver).send(serializedMsg);
    }

}
