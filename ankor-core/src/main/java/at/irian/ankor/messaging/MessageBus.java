package at.irian.ankor.messaging;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Manfred Geiler
 */
public abstract class MessageBus<S> implements MessageSender {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MessageBus.class);

    private final MessageSerializer<S> messageSerializer;
    private final MessageDeserializer<S> messageDeserializer;
    private final List<MessageListener> messageListeners = new ArrayList<MessageListener>();

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
    public void sendMessage(Message msg) {
        sendSerializedMessage(messageSerializer.serialize(msg));
    }

    public void registerMessageListener(MessageListener messageListener) {
        messageListeners.add(messageListener);
    }

    public void unregisterMessageListener(MessageListener messageListener) {
        messageListeners.remove(messageListener);
    }

    public void receiveMessage(Message msg) {
        if (msg instanceof ActionMessage) {
            for (MessageListener messageListener : messageListeners) {
                try {
                    messageListener.onActionMessage((ActionMessage) msg);
                } catch (Exception e) {
                    LOG.error("ActionMessageListener " + messageListener + " failed on message " + msg, e);
                }
            }
        } else if (msg instanceof ChangeMessage) {
            for (MessageListener messageListener : messageListeners) {
                try {
                    messageListener.onChangeMessage((ChangeMessage) msg);
                } catch (Exception e) {
                    LOG.error("ChangeMessageListener " + messageListener + " failed on message " + msg, e);
                }
            }
        } else {
            throw new IllegalArgumentException("Unsupported message type " + msg.getClass());
        }
    }

    protected abstract void sendSerializedMessage(S msg);

    public void receiveSerializedMessage(S msg) {
        receiveMessage(messageDeserializer.deserialize(msg));
    }
}
