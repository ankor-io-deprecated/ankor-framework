package at.irian.ankor.messaging;

import at.irian.ankor.connection.RemoteSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Manfred Geiler
 */
@Deprecated
public abstract class MessageBus<S> implements MessageSenderProvider {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MessageBus.class);

    protected final MessageSerializer<S> messageSerializer;
    protected final MessageDeserializer<S> messageDeserializer;
    protected final List<MessageListener> messageListeners = new ArrayList<MessageListener>();

    @SuppressWarnings("UnusedDeclaration")
    protected MessageBus(MessageSerializer<S> messageSerializer, MessageDeserializer<S> messageDeserializer) {
        this.messageSerializer = messageSerializer;
        this.messageDeserializer = messageDeserializer;
    }

    protected MessageBus(MessageMapper<S> messageMapper) {
        this.messageSerializer = messageMapper;
        this.messageDeserializer = messageMapper;
    }

    @Override
    public MessageSender getMessageSenderFor(final RemoteSystem remoteSystem) {
        return new MessageSender() {
            @Override
            public void sendMessage(Message msg) {
                sendSerializedMessage(remoteSystem.getId(), messageSerializer.serialize(msg));
            }

        };
    }

    public void registerMessageListener(MessageListener messageListener) {
        messageListeners.add(messageListener);
    }

    public void unregisterMessageListener(MessageListener messageListener) {
        messageListeners.remove(messageListener);
    }

    public void receiveMessage(Message msg) {
        boolean handled = false;
        for (MessageListener messageListener : messageListeners) {
            if (msg.isAppropriateListener(messageListener)) {
                handled = true;
                try {
                    msg.processBy(messageListener);
                } catch (Exception e) {
                    LOG.error("MessageListener " + messageListener + " failed on message " + msg, e);
                }
            }
        }
        if (!handled) {
            throw new IllegalArgumentException("Unsupported message type " + msg.getClass());
        }
    }

    protected abstract void sendSerializedMessage(String remoteSystemId, S msg);

    public void receiveSerializedMessage(S msg) {
        receiveMessage(messageDeserializer.deserialize(msg, Message.class));
    }
}
