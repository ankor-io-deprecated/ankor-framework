package at.irian.ankor.messaging;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class SocketMessageLoop<S> extends AbstractMessageLoop<S> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketMessageLoop.class);

    private final String remoteHost;
    private final int remotePort;
    private final int localPort;
    private ServerSocket serverSocket;

    public SocketMessageLoop(String name, MessageMapper<S> messageMapper, String remoteHost, int remotePort, int localPort) {
        super(name, messageMapper);
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.localPort = localPort;
    }

    @Override
    protected void send(S msg) {
        Socket socket;
        try {
            socket = new Socket(remoteHost, remotePort);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot connect to " + remoteHost + ":" + remotePort);
        }

        try {
            OutputStreamWriter outWriter = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
            PrintWriter writer = new PrintWriter(outWriter, true);
            writer.println(msg);
        } catch (IOException e) {
            throw new RuntimeException("Error sending message to " + socket);
        }
    }

    @Override
    protected S receive() throws InterruptedException {
        Socket accept = null;
        try {
            accept = serverSocket.accept();
            InputStreamReader inReader = new InputStreamReader(accept.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(inReader);
            //noinspection unchecked
            return (S)reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException("Error reading message from " + serverSocket);
        } finally {
            if (accept != null) {
                try {
                    accept.close();
                } catch (IOException ignored) {}
            }
        }
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void start() {
        try {
            serverSocket = new ServerSocket(localPort);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot open local server socket");
        }

        super.start();
    }

    @Override
    public void stop() {
        super.stop();

        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot close local server socket");
        }
    }
}
