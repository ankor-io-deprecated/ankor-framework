package at.irian.ankor.connector.local;

import at.irian.ankor.connector.Connector;
import at.irian.ankor.msg.*;
import at.irian.ankor.system.AnkorSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Manfred Geiler
 */
@SuppressWarnings("UnusedDeclaration")  // indirectly called by ServiceLoader
public class LocalModelSessionConnector implements Connector {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LocalModelSessionConnector.class);

    private MessageBus messageBus;
    private List<MessageListener> messageListeners;

    @Override
    public void init(AnkorSystem system) {
        messageBus = system.getMessageBus();
        messageListeners = new ArrayList<MessageListener>();
        messageListeners.add(new LocalModelSessionConnectMessageListener(system.getModelSessionManager(),
                                                                  system.getRoutingTable(),
                                                                  system.getApplication(), messageBus));
        messageListeners.add(new LocalModelSessionEventMessageListener(system.getModelSessionManager(),
                                                                system.getRoutingTable(),
                                                                system.getModifier()));
        messageListeners.add(new LocalModelSessionCloseMessageListener(system.getModelSessionManager()));
    }

    @Override
    public void start() {
        for (MessageListener listener : messageListeners) {
            messageBus.registerMessageListener(listener);
        }
    }

    @Override
    public void stop() {
        for (MessageListener listener : messageListeners) {
            messageBus.unregisterMessageListener(listener);
        }
    }

}
