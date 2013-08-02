package at.irian.ankor.socket;

import at.irian.ankor.messaging.AbstractMessageLoop;
import at.irian.ankor.messaging.MessageMapper;
import at.irian.ankor.session.RemoteSystem;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class SocketMessageLoop<S> extends AbstractMessageLoop<S> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketMessageLoop.class);

    private final int localPort;
    private final Map<String, Host> remoteSystems = new HashMap<String, Host>();
    private ServerSocket listenSocket;

    public SocketMessageLoop(Host localHost, MessageMapper<S> messageMapper) {
        super(localHost.getId(), messageMapper);
        this.localPort = localHost.getPort();
    }

    public void addRemoteSystem(Host remoteHost) {
        remoteSystems.put(remoteHost.getId(), remoteHost);
    }

    @Override
    protected void send(String remoteSystemId, S msg) {

        Host remoteSystem = remoteSystems.get(remoteSystemId);
        if (remoteSystem == null) {
            throw new IllegalArgumentException("Unknown remote system " + remoteSystemId);
        }

        Socket socket;
        try {
            socket = new Socket(remoteSystem.getHostName(), remoteSystem.getPort());
        } catch (IOException e) {
            throw new IllegalStateException("Cannot connect to " + remoteSystem);
        }

        try {
            OutputStreamWriter outWriter = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
            PrintWriter writer = new PrintWriter(outWriter, true);
            writer.println(msg);
        } catch (IOException e) {
            throw new RuntimeException("Error sending message to " + socket);
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }

    @Override
    protected S receive() throws InterruptedException {
        Socket accept = null;
        try {
            accept = listenSocket.accept();
            InputStreamReader inReader = new InputStreamReader(accept.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(inReader);
            //noinspection unchecked
            return (S)reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException("Error reading message from " + listenSocket);
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
    public void start(boolean daemon) {
        try {
            listenSocket = new ServerSocket(localPort);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot open local server socket");
        }

        super.start(daemon);
    }

    @Override
    public void stop() {
        super.stop();

        try {
            listenSocket.close();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot close local server socket");
        }
    }


    @Override
    protected Collection<? extends RemoteSystem> getKnownRemoteSystems() {
        return remoteSystems.values();
    }

    public static class Host implements RemoteSystem {

        private final String id;
        private final String hostName;
        private final int port;

        public Host(String id, String hostName, int port) {
            this.id = id;
            this.hostName = hostName;
            this.port = port;
        }

        @Override
        public String getId() {
            return id;
        }

        public String getHostName() {
            return hostName;
        }

        public int getPort() {
            return port;
        }

        @Override
        public String toString() {
            return hostName + ":" + port;
        }
    }

}
