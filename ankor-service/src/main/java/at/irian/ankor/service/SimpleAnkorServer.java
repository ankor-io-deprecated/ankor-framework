package at.irian.ankor.service;

import at.irian.ankor.action.Action;
import at.irian.ankor.application.DefaultApplication;
import at.irian.ankor.messaging.LoopbackMessageBus;
import at.irian.ankor.messaging.MessageBus;
import at.irian.ankor.messaging.MessageFactory;
import at.irian.ankor.messaging.json.JsonMessageMapper;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class SimpleAnkorServer extends ELAnkorServer {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleAnkorServer.class);

    private final String serverName;

    private SimpleAnkorServer(DefaultApplication application,
                              String serverName,
                              MessageFactory messageFactory,
                              MessageBus messageBus) {
        super(application, messageFactory, messageBus);
        this.serverName = serverName;
    }

    public static SimpleAnkorServer create(DefaultApplication application, String serverName) {
        JsonMessageMapper messageMapper = new JsonMessageMapper(application.getRefFactory());
        LoopbackMessageBus<String> messageBus = new LoopbackMessageBus<String>(messageMapper, serverName);
        MessageFactory messageFactory = new MessageFactory();
        return new SimpleAnkorServer(application,
                                     serverName,
                                     messageFactory,
                                     messageBus);

    }

    @SuppressWarnings("unchecked")
    public void setRemoteServer(SimpleAnkorServer remoteServer) {
        LoopbackMessageBus<String> thisMessageBus = (LoopbackMessageBus) this.getMessageBus();
        LoopbackMessageBus<String> thatMessageBus = (LoopbackMessageBus) remoteServer.getMessageBus();
        thisMessageBus.connectTo(thatMessageBus);
        thatMessageBus.connectTo(thisMessageBus);
    }

    @Override
    public String toString() {
        return serverName;
    }

    public void receiveAction(String modelContextPath, Action action) {
        receiveAction(getApplication().getRefFactory().ref(modelContextPath), action);
    }

    public void receiveChange(String changedPath, Object newValue) {
        receiveChange(null, changedPath, newValue);
    }

    public void receiveChange(String modelContextPath, String changedPath, Object newValue) {
        receiveChange(modelContextPath != null ? getApplication().getRefFactory().ref(modelContextPath) : null,
                      getApplication().getRefFactory().ref(changedPath),
                      newValue);
    }

}
