package at.irian.ankor.socket;

import at.irian.ankor.messaging.ActionMessage;
import at.irian.ankor.messaging.MessageMapper;

/**
 * @author Manfred Geiler
 */
public class ServerSocketMessageLoop<S> extends SocketMessageLoop<S>  {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ServerSocketMessageLoop.class);

    public ServerSocketMessageLoop(Host localHost, MessageMapper<S> messageMapper) {
        super(localHost, messageMapper);
    }

    @Override
    public void start(boolean daemon) {

        getMessageBus().registerMessageListener(new ActionMessage.Listener() {
            @Override
            public void onActionMessage(ActionMessage msg) {
                if (msg.getAction().getName().equals(SocketConnectAction.ACTION_NAME)) {
                    Host clientHost = new Host(msg.getSenderId(),
                                               (String) msg.getAction().getParams().get("hostname"),
                                               (Integer) msg.getAction().getParams().get("port"));
                    addRemoteSystem(clientHost);
                    LOG.info("New client registered: {}", clientHost);
                }
            }
        });

        super.start(daemon);
    }
}
