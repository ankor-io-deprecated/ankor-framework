package at.irian.ankor.connector.socket;

import at.irian.ankor.action.Action;
import at.irian.ankor.change.Change;
import at.irian.ankor.messaging.MessageDeserializer;
import at.irian.ankor.msg.ActionEventMessage;
import at.irian.ankor.msg.ChangeEventMessage;
import at.irian.ankor.msg.ConnectMessage;
import at.irian.ankor.msg.MessageBus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
* @author Manfred Geiler
*/
public class SocketListener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketListener.class);

    private final String systemName;
    private final int listenPort;
    private final MessageDeserializer<String> messageDeserializer;
    private final MessageBus messageBus;
    private final Map<String, SocketParty> socketParties;
    private volatile boolean started;
    private Thread receiveLoopThread;

    public SocketListener(String systemName,
                          int listenPort,
                          MessageDeserializer<String> messageDeserializer,
                          MessageBus messageBus) {
        this.systemName = systemName;
        this.listenPort = listenPort;
        this.messageDeserializer = messageDeserializer;
        this.messageBus = messageBus;
        this.socketParties = new ConcurrentHashMap<String, SocketParty>();
    }

    public int getListenPort() {
        return listenPort;
    }

    public void start() {

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
        LOG.info("{} started", this);
    }

    public void stop() {
        this.started = false;
        this.receiveLoopThread.interrupt();
    }

    private void loop(ServerSocket listenSocket) {
        boolean interrupted = false;
        LOG.debug("SocketConnector for {} is listening...", systemName);
        while (started && !interrupted) {
            try {
                String serializedMsg = receive(listenSocket);
                LOG.trace("SocketConnector for {} receives {}", systemName, serializedMsg);
                try {
                    SocketMessage socketMessage = messageDeserializer.deserialize(serializedMsg, SocketMessage.class);
                    if (socketMessage.getAction() != null) {
                        handleIncomingActionMessage(socketMessage);
                    }
                    if (socketMessage.getChange() != null) {
                        handleIncomingChangeMessage(socketMessage);
                    }
                } catch (Exception e) {
                    LOG.error("Exception while handling socket message " + serializedMsg);
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

    private void handleIncomingActionMessage(SocketMessage socketMessage) {
        Action action = socketMessage.getAction();
        if (SocketConnectAction.ACTION_NAME.equals(action.getName())) {
            SocketParty socketParty = new SocketParty(socketMessage.getSenderId(),
                                                      (String) action.getParams().get("hostname"),
                                                      Integer.parseInt((String) action.getParams().get("port")));
            socketParties.put(socketParty.getId(), socketParty);
            messageBus.broadcast(new ConnectMessage(socketParty, null));
        } else {
            SocketParty socketParty = socketParties.get(socketMessage.getSenderId());
            if (socketParty == null) {
                throw new IllegalStateException("Unknown remote sender id " + socketMessage.getSenderId());
            }
            messageBus.broadcast(new ActionEventMessage(socketParty, socketMessage.getProperty(), action));
        }
    }

    private void handleIncomingChangeMessage(SocketMessage socketMessage) {
        Change change = socketMessage.getChange();
        SocketParty socketParty = socketParties.get(socketMessage.getSenderId());
        if (socketParty == null) {
            throw new IllegalStateException("Unknown remote sender id " + socketMessage.getSenderId());
        }
        messageBus.broadcast(new ChangeEventMessage(socketParty, socketMessage.getProperty(), change));
    }

    public void removeSocketParty(SocketParty party) {
        socketParties.remove(party.getId());
    }
}
