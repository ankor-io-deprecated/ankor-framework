package at.irian.ankor.servlet;

import at.irian.ankor.messaging.Message;
import at.irian.ankor.messaging.MessageBus;
import at.irian.ankor.messaging.MessageSender;
import at.irian.ankor.messaging.json.viewmodel.ViewModelJsonMessageMapper;
import at.irian.ankor.session.RemoteSystem;
import at.irian.ankor.session.SimpleRemoteSystem;

import java.util.*;

/**
 * @author Manfred Geiler
 */
public class ServletMessageBus extends MessageBus<String> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ServletMessageBus.class);

    private final Map<RemoteSystem, List<Message>> pendingClientMessages;
    private final ViewModelJsonMessageMapper messageMapper;

    public ServletMessageBus(ViewModelJsonMessageMapper messageMapper) {
        super(messageMapper, messageMapper);
        this.messageMapper = messageMapper;
        this.pendingClientMessages = new HashMap<RemoteSystem, List<Message>>();
    }

    public ViewModelJsonMessageMapper getMessageMapper() {
        return messageMapper;
    }

    @Override
    public Collection<? extends RemoteSystem> getKnownRemoteSystems() {
        synchronized (pendingClientMessages) {
            return new HashSet<RemoteSystem>(pendingClientMessages.keySet());
        }
    }

    @Override
    public MessageSender getMessageSenderFor(final RemoteSystem remoteSystem) {
        return new MessageSender() {
            @Override
            public void sendMessage(Message msg) {
                synchronized (pendingClientMessages) {
                    List<Message> pendingMessages = pendingClientMessages.get(remoteSystem);
                    if (pendingMessages == null) {
                        pendingMessages = new ArrayList<Message>();
                        pendingClientMessages.put(remoteSystem, pendingMessages);
                    }
                    pendingMessages.add(msg);
                }
            }
        };
    }

    @Override
    protected void sendSerializedMessage(String remoteSystemId, String msg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void receiveSerializedMessage(String msg) {
        Message[] messages = messageMapper.deserializeArray(msg);
        for (Message message : messages) {
            receiveMessage(message);
        }
    }

    public List<Message> getAndClearPendingMessagesFor(String remoteSystemId) {
        synchronized (pendingClientMessages) {
            List<Message> messages = pendingClientMessages.get(new SimpleRemoteSystem(remoteSystemId));
            List<Message> result = messages != null ? new ArrayList<Message>(messages) : Collections.<Message>emptyList();
            pendingClientMessages.clear();
            return result;
        }
    }
}
