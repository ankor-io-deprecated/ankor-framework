package at.irian.ankor.messaging;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Manfred Geiler
 */
public abstract class MessageBus<S> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MessageBus.class);

    private final MessageSerializer<S> messageSerializer;
    private final MessageDeserializer<S> messageDeserializer;
    private final List<MessageListener> messageListeners = new ArrayList<MessageListener>();

    protected MessageBus(MessageSerializer<S> messageSerializer, MessageDeserializer<S> messageDeserializer) {
        this.messageSerializer = messageSerializer;
        this.messageDeserializer = messageDeserializer;
    }

    public void sendMessage(Message msg) {
        sendSerializedMessage(messageSerializer.serialize(msg));
    }

    public void registerMessageListener(MessageListener messageListener) {
        messageListeners.add(messageListener);
    }

    protected void receiveMessage(Message msg) {
        if (msg instanceof ActionMessage) {
            for (MessageListener messageListener : messageListeners) {
                messageListener.onActionMessage((ActionMessage) msg);
            }
        } else if (msg instanceof ChangeMessage) {
            for (MessageListener messageListener : messageListeners) {
                messageListener.onChangeMessage((ChangeMessage) msg);
            }
        } else {
            throw new IllegalArgumentException("Unsupported message type " + msg.getClass());
        }
    }

    protected abstract void sendSerializedMessage(S msg);

    protected void receiveSerializedMessage(S msg) {
        receiveMessage(messageDeserializer.deserialize(msg));
    }
}
