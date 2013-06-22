package at.irian.ankor.messaging;

/**
 * @author Manfred Geiler
 */
public class LoopbackMessageBus<S> extends MessageBus<S> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LoopbackMessageBus.class);

    private final String name;
    private MessageBus<S> connectedMessageBus;

    public LoopbackMessageBus(MessageMapper<S> messageMapper, String name) {
        super(messageMapper);
        this.name = name;
    }

    public void connectTo(MessageBus<S> messageBus) {
        this.connectedMessageBus = messageBus;
    }

    @Override
    protected void sendSerializedMessage(S msg) {
        if (connectedMessageBus == null) {
            throw new IllegalStateException("No connected message bus");
        }
        LOG.debug("msg from {} to {}: {}", this, connectedMessageBus, msg);
        connectedMessageBus.receiveSerializedMessage(msg);
    }

    @Override
    public String toString() {
        return name;
    }
}
