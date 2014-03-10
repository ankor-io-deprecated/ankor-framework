package at.irian.ankor.switching.connector.socket;

import at.irian.ankor.messaging.MessageSerializer;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author Manfred Geiler
 */
public class SocketSender {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketSender.class);

    private final MessageSerializer<String> messageSerializer;

    public SocketSender(MessageSerializer<String> messageSerializer) {
        this.messageSerializer = messageSerializer;
    }

    public void send(SocketModelAddress receiver, SocketMessage socketMessage) throws IOException {
        sendString(receiver, messageSerializer.serialize(socketMessage));
    }

    private void sendString(SocketModelAddress receiver, String serializedMsg) throws IOException {
        Socket socket = new Socket(receiver.getHost(), receiver.getPort());
        try {

            LOG.debug("Sending serialized message to {}: {}", receiver, serializedMsg);

            OutputStreamWriter outWriter = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
            PrintWriter writer = new PrintWriter(outWriter, true);
            writer.println(serializedMsg);

        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }

}
