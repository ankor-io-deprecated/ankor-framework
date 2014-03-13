package at.irian.ankor.servlet.polling;

import at.irian.ankor.messaging.Message;
import at.irian.ankor.messaging.MessageBus;
import at.irian.ankor.messaging.MessageSender;
import at.irian.ankor.messaging.json.viewmodel.ViewModelJsonMessageMapper;
import at.irian.ankor.connection.RemoteSystem;
import at.irian.ankor.connection.SimpleRemoteSystem;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Manfred Geiler
 */
@Deprecated
public class ServletMessageBus extends MessageBus<String> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ServletMessageBus.class);

    private final Map<RemoteSystem, Queue<Message>> clientOutQueues;
    private final ViewModelJsonMessageMapper messageMapper;

    public ServletMessageBus(ViewModelJsonMessageMapper messageMapper) {
        super(messageMapper, messageMapper);
        this.messageMapper = messageMapper;
        this.clientOutQueues = new ConcurrentHashMap<RemoteSystem, Queue<Message>>();
    }

    public ViewModelJsonMessageMapper getMessageMapper() {
        return messageMapper;
    }

    @Override
    public Collection<? extends RemoteSystem> getKnownRemoteSystems() {
        return Collections.unmodifiableCollection(clientOutQueues.keySet());
    }

    @Override
    public MessageSender getMessageSenderFor(final RemoteSystem remoteSystem) {
        return new MessageSender() {
            @Override
            public void sendMessage(Message msg) {
                getOutQueueFor(remoteSystem).add(msg);
            }
        };
    }

    private Queue<Message> getOutQueueFor(RemoteSystem remoteSystem) {
        Queue<Message> outQueue = clientOutQueues.get(remoteSystem);
        if (outQueue == null) {
            synchronized (clientOutQueues) {
                outQueue = clientOutQueues.get(remoteSystem);
                if (outQueue == null) {
                    outQueue = new LinkedBlockingQueue<Message>();
                    clientOutQueues.put(remoteSystem, outQueue);
                }
            }
        }
        return outQueue;
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

    public Collection<Message> getPendingMessagesFor(String remoteSystemId) {
        List<Message> messages = null;

        Queue<Message> queue = clientOutQueues.get(new SimpleRemoteSystem(remoteSystemId));
        if (queue != null) {
            Message poll = queue.poll();
            while (poll != null) {
                if (messages == null) {
                    messages = new ArrayList<Message>();
                }
                messages.add(poll);
                poll = queue.poll();
            }
        }

        return messages != null ? messages : Collections.<Message>emptyList();
    }
}
