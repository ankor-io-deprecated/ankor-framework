package at.irian.ankor.connector.socket;

import at.irian.ankor.action.Action;
import at.irian.ankor.change.Change;
import at.irian.ankor.event.source.PartySource;
import at.irian.ankor.messaging.MessageDeserializer;
import at.irian.ankor.msg.ActionEventMessage;
import at.irian.ankor.msg.ChangeEventMessage;
import at.irian.ankor.msg.ConnectRequestMessage;
import at.irian.ankor.msg.MessageBus;
import at.irian.ankor.path.PathSyntax;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;

/**
* @author Manfred Geiler
*/
public class SocketListener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketListener.class);

    private final String systemName;
    private final int listenPort;
    private final MessageDeserializer<String> messageDeserializer;
    private final MessageBus messageBus;
    private final PathSyntax pathSyntax;
    private volatile boolean started;
    private Thread receiveLoopThread;

    public SocketListener(String systemName,
                          URI localAddress,
                          MessageDeserializer<String> messageDeserializer,
                          MessageBus messageBus,
                          PathSyntax pathSyntax) {
        this.systemName = systemName;
        this.listenPort = localAddress.getPort();
        this.messageDeserializer = messageDeserializer;
        this.messageBus = messageBus;
        this.pathSyntax = pathSyntax;
    }

    public int getListenPort() {
        return listenPort;
    }

    public void start() {
        LOG.info("Staring {} ...", this);

        final ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(listenPort);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot open server socket on port " + listenPort);
        }

        this.receiveLoopThread = new Thread(new Runnable() {
            @Override
            public void run() {
                loop(serverSocket);
            }
        }, "Ankor '" + systemName + "'");
        this.receiveLoopThread.setDaemon(true);

        this.started = true;
        this.receiveLoopThread.start();
        LOG.info("{} successfully started", this);
    }

    public void stop() {
        this.started = false;
        this.receiveLoopThread.interrupt();
    }

    private void loop(ServerSocket listenSocket) {
        boolean interrupted = false;
        LOG.debug("SocketListener for '{}' is listening...", systemName);
        while (started && !interrupted) {
            try {
                String serializedMsg = receive(listenSocket);
                LOG.debug("SocketListener for '{}' receives {}", systemName, serializedMsg);
                try {
                    SocketMessage socketMessage = messageDeserializer.deserialize(serializedMsg, SocketMessage.class);
                    if (socketMessage.getAction() != null) {
                        handleIncomingActionEventMessage(socketMessage);
                    } else if (socketMessage.getChange() != null) {
                        handleIncomingChangeEventMessage(socketMessage);
                    } else {
                        handleIncomingConnectMessage(socketMessage);
                    }
                } catch (Exception e) {
                    LOG.error("Exception while handling socket message " + serializedMsg, e);
                }
            } catch (InterruptedException e) {
                interrupted = true;
            }
        }
        Thread.currentThread().interrupt();
    }

    private String receive(ServerSocket listenSocket) throws InterruptedException {
        Socket accept = null;
        try {
            accept = listenSocket.accept();
            InputStreamReader inReader = new InputStreamReader(accept.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(inReader);
            return reader.readLine();
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

    private void handleIncomingActionEventMessage(SocketMessage socketMessage) {
        Action action = socketMessage.getAction();
        String modelName = pathSyntax.rootOf(socketMessage.getProperty());
        URI senderAddress = URI.create(socketMessage.getSenderId());
        SocketParty sender = new SocketParty(senderAddress, modelName);
        messageBus.broadcast(new ActionEventMessage(sender, new PartySource(sender, this), socketMessage.getProperty(), action));
    }

    private void handleIncomingChangeEventMessage(SocketMessage socketMessage) {
        Change change = socketMessage.getChange();
        String modelName = pathSyntax.rootOf(socketMessage.getProperty());
        URI senderAddress = URI.create(socketMessage.getSenderId());
        SocketParty sender = new SocketParty(senderAddress, modelName);
        messageBus.broadcast(new ChangeEventMessage(sender, new PartySource(sender, this), socketMessage.getProperty(), change));
    }

    private void handleIncomingConnectMessage(SocketMessage socketMessage) {
        String modelName = pathSyntax.rootOf(socketMessage.getProperty());
        URI senderAddress = URI.create(socketMessage.getSenderId());
        SocketParty sender = new SocketParty(senderAddress, modelName);
        messageBus.broadcast(new ConnectRequestMessage(sender, modelName, socketMessage.getConnectParams()));
    }


    @Override
    public String toString() {
        return "SocketListener{" +
               "systemName='" + systemName + '\'' +
               ", listenPort=" + listenPort +
               '}';
    }
}
