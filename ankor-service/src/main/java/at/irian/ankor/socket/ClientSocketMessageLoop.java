package at.irian.ankor.socket;

import at.irian.ankor.messaging.Message;
import at.irian.ankor.messaging.MessageFactory;
import at.irian.ankor.messaging.MessageMapper;
import at.irian.ankor.messaging.MessageSender;
import at.irian.ankor.session.RemoteSystem;

/**
 * @author Manfred Geiler
 */
public class ClientSocketMessageLoop<S> extends SocketMessageLoop<S> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ClientSocketMessageLoop.class);

    private final Host localHost;
    private final MessageFactory messageFactory;

    public ClientSocketMessageLoop(Host localHost,
                                   MessageMapper<S> messageMapper,
                                   Host remoteHost,
                                   MessageFactory messageFactory) {
        super(localHost, messageMapper);
        this.localHost = localHost;
        this.messageFactory = messageFactory;
        addRemoteSystem(remoteHost);
    }

    private RemoteSystem getServerRemoteSystem() {
        return getKnownRemoteSystems().iterator().next();
    }

    @Override
    public void start(boolean daemon) {
        MessageSender messageSenderToServer = getMessageBus().getMessageSenderFor(getServerRemoteSystem());
        Message connectMsg = messageFactory.createGlobalActionMessage(SocketConnectAction.create(localHost.getHostName(),
                                                                                          localHost.getPort()));
        messageSenderToServer.sendMessage(connectMsg);

        super.start(daemon);    //To change body of overridden methods use File | Settings | File Templates.
    }

}
