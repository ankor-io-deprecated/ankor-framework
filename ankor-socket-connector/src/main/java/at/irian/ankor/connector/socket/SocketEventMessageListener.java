package at.irian.ankor.connector.socket;

import at.irian.ankor.messaging.MessageSerializer;
import at.irian.ankor.msg.ActionEventMessage;
import at.irian.ankor.msg.ChangeEventMessage;
import at.irian.ankor.msg.EventMessage;
import at.irian.ankor.msg.SwitchingCenter;
import at.irian.ankor.msg.party.Party;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collection;

/**
* @author Manfred Geiler
*/
class SocketEventMessageListener implements EventMessage.Listener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketEventMessageListener.class);

    private final SwitchingCenter switchingCenter;
    private final MessageSerializer<String> messageSerializer;

    public SocketEventMessageListener(SwitchingCenter switchingCenter, MessageSerializer<String> messageSerializer) {
        this.switchingCenter = switchingCenter;
        this.messageSerializer = messageSerializer;
    }

    @Override
    public void onEventMessage(EventMessage msg) {
        Party sender = msg.getSender();
        Collection<Party> receivers = switchingCenter.getConnectedParties(sender);
        for (Party receiver : receivers) {
            if (receiver instanceof SocketParty) {
                send((SocketParty) receiver, msg);
            }
        }
    }


    private void send(SocketParty socketParty, EventMessage eventMessage) {

        Socket socket;
        try {
            socket = new Socket(socketParty.getHost(), socketParty.getPort());
        } catch (IOException e) {
            throw new IllegalStateException("Cannot connect to " + socketParty);
        }

        SocketMessage socketMessage;
        if (eventMessage instanceof ActionEventMessage) {
            socketMessage = new SocketMessage(null,
                                              ((ActionEventMessage) eventMessage).getProperty(),
                                              ((ActionEventMessage) eventMessage).getAction(),
                                              null);
        } else if (eventMessage instanceof ChangeEventMessage) {
            socketMessage = new SocketMessage(null,
                                              ((ChangeEventMessage) eventMessage).getProperty(),
                                              null,
                                              ((ChangeEventMessage) eventMessage).getChange());
        } else {
            throw new IllegalArgumentException("Unsupported event message type " + eventMessage.getClass().getName());
        }

        String serializedMsg = messageSerializer.serialize(socketMessage);
        LOG.debug("Sending serialized message to {}: {}", socketParty, serializedMsg);

        try {
            OutputStreamWriter outWriter = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
            PrintWriter writer = new PrintWriter(outWriter, true);
            writer.println(serializedMsg);
        } catch (IOException e) {
            throw new RuntimeException("Error sending message to " + socket);
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }

}
