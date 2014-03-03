package at.irian.ankor.connector.socket;

import at.irian.ankor.connector.Connector;
import at.irian.ankor.messaging.MessageMapper;
import at.irian.ankor.messaging.MessageMapperFactory;
import at.irian.ankor.msg.MessageBus;
import at.irian.ankor.msg.MessageListener;
import at.irian.ankor.system.AnkorSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Manfred Geiler
 */
public class SocketConnector implements Connector {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketConnector.class);

    public static final String MESSAGE_MAPPER_ATTR = SocketConnector.class.getName() + "MESSAGE_MAPPER";

    private boolean enabled;
    private SocketListener socketListener;
    private MessageBus messageBus;
    private List<MessageListener> messageListeners;

    @Override
    public void init(final AnkorSystem system) {
        this.enabled = system.getConfig().getBoolean("at.irian.ankor.connector.socket.SocketConnector.enabled");
        if (!enabled) {
            LOG.info("SocketConnector not initialized because it is disabled");
            return;
        }

        int localPort = system.getConfig().getInt("at.irian.ankor.connector.socket.SocketConnector.localPort");

        MessageMapper<String> messageMapper = new MessageMapperFactory<String>(system).createMessageMapper();

        this.messageBus = system.getMessageBus();

        this.socketListener = new SocketListener(system.getSystemName(),
                                                 localPort,
                                                 messageMapper,
                                                 system.getMessageBus());

        this.messageListeners = new ArrayList<MessageListener>();
        this.messageListeners.add(new SocketEventMessageListener(system.getSwitchingCenter(), messageMapper));
        this.messageListeners.add(new SocketCloseMessageListener(socketListener));
    }

    @Override
    public void start() {
        if (!enabled) {
            LOG.debug("SocketConnector not started because it is disabled");
            return;
        }

        LOG.info("Starting SocketConnector (listening on port " + socketListener.getListenPort() + ")");
        socketListener.start();
        for (MessageListener listener : messageListeners) {
            messageBus.registerMessageListener(listener);
        }
        LOG.debug("SocketConnector was started");
    }

    @Override
    public void stop() {
        if (!enabled) {
            return;
        }

        LOG.info("Stopping SocketConnector (listening on port " + socketListener.getListenPort() + ")");
        socketListener.stop();
        for (MessageListener listener : messageListeners) {
            messageBus.unregisterMessageListener(listener);
        }
        LOG.debug("SocketConnector was stopped");
    }

}
