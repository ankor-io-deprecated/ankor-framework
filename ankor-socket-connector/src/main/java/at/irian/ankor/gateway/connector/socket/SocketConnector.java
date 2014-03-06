package at.irian.ankor.gateway.connector.socket;

import at.irian.ankor.gateway.connector.Connector;
import at.irian.ankor.gateway.party.SocketParty;
import at.irian.ankor.messaging.MessageMapper;
import at.irian.ankor.messaging.MessageMapperFactory;
import at.irian.ankor.gateway.Gateway;
import at.irian.ankor.path.el.SimpleELPathSyntax;
import at.irian.ankor.system.AnkorSystem;

import java.net.URI;

/**
 * @author Manfred Geiler
 */
public class SocketConnector implements Connector {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketConnector.class);

    private boolean enabled;
    private SocketListener socketListener;
    private Gateway gateway;
    private URI localAddress;
    private MessageMapper<String> messageMapper;

    @Override
    public void init(final AnkorSystem system) {
        this.enabled = system.getConfig().getBoolean("at.irian.ankor.gateway.connector.socket.SocketConnector.enabled");
        if (!enabled) {
            LOG.info("SocketConnector not initialized because it is disabled - see ./ankor-socket-connector/src/main/resources/reference.conf for details on how to enable socket support");
            return;
        }

        this.localAddress = URI.create(system.getConfig().getString("at.irian.ankor.gateway.connector.socket.SocketConnector.localAddress"));
        this.messageMapper = new MessageMapperFactory<String>(system).createMessageMapper();

        this.gateway = system.getGateway();

        this.socketListener = new SocketListener(system.getSystemName(),
                                                 this.localAddress,
                                                 this.messageMapper,
                                                 this.gateway,
                                                 SimpleELPathSyntax.getInstance());
    }

    @Override
    public void start() {
        if (!enabled) {
            LOG.debug("SocketConnector not started because it is disabled");
            return;
        }

        LOG.info("Starting SocketConnector (listening on port " + socketListener.getListenPort() + ")");
        socketListener.start();

        gateway.registerMessageDeliverer(SocketParty.class, new SocketDeliverHandler(localAddress, messageMapper, gateway));
        gateway.registerDisconnectHandler(SocketParty.class, new SocketCloseHandler());

        LOG.debug("SocketConnector successfully started");
    }

    @Override
    public void stop() {
        if (!enabled) {
            return;
        }

        LOG.info("Stopping SocketConnector (listening on port " + socketListener.getListenPort() + ")");
        socketListener.stop();

        gateway.unregisterMessageDeliverer(SocketParty.class);
        gateway.unregisterDisconnectHandler(SocketParty.class);

        LOG.debug("SocketConnector was stopped");
    }

}
