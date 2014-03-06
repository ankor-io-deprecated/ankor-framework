package at.irian.ankor.gateway.connector.socket;

import at.irian.ankor.gateway.handler.DeliverHandler;
import at.irian.ankor.gateway.Gateway;
import at.irian.ankor.gateway.msg.ActionEventGatewayMsg;
import at.irian.ankor.gateway.msg.ChangeEventGatewayMsg;
import at.irian.ankor.gateway.msg.GatewayMsg;
import at.irian.ankor.gateway.party.SocketParty;
import at.irian.ankor.messaging.MessageSerializer;
import at.irian.ankor.gateway.party.Party;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class SocketDeliverHandler implements DeliverHandler<SocketParty> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketDeliverHandler.class);

    private final URI localAddress;
    private final MessageSerializer<String> messageSerializer;
    private final Gateway gateway;

    public SocketDeliverHandler(URI localAddress,
                                MessageSerializer<String> messageSerializer,
                                Gateway gateway) {
        this.localAddress = localAddress;
        this.messageSerializer = messageSerializer;
        this.gateway = gateway;
    }

    @Override
    public void open(Party sender, SocketParty receiver, Map<String, Object> connectParameters) {
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
    public void deliver(Party sender, SocketParty receiver, GatewayMsg message) {
        try {
            send(receiver, message);
        } catch (IOException e) {
            LOG.error("Error sending msg {} to {} - party is getting disconnected", message, receiver);
            gateway.routeClose(receiver);
        }
    }

    @Override
    public void close(Party sender, SocketParty receiver) {
        try {
            SocketMessage socketMessage = SocketMessage.createCloseMsg(localAddress.toString(),
                                                                       sender.getModelName());
            sendSocketMessage(receiver, socketMessage);
        } catch (IOException e) {
            LOG.error("Error sending close msg from {} to {}", sender, receiver);
        }
    }

    private void send(SocketParty receiver, GatewayMsg message) throws IOException {
        SocketMessage socketMessage;

        if (message instanceof ActionEventGatewayMsg) {
            socketMessage = SocketMessage.createActionMsg(localAddress.toString(),
                                                          ((ActionEventGatewayMsg) message).getProperty(),
                                                          ((ActionEventGatewayMsg) message).getAction());
        } else if (message instanceof ChangeEventGatewayMsg) {
            socketMessage = SocketMessage.createChangeMsg(localAddress.toString(),
                                                          ((ChangeEventGatewayMsg) message).getProperty(),
                                                          ((ChangeEventGatewayMsg) message).getChange());
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
