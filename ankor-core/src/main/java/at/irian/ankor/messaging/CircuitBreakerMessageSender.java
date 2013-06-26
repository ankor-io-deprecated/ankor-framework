package at.irian.ankor.messaging;

/**
 * @author Manfred Geiler
 */
public class CircuitBreakerMessageSender implements MessageSender {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(CircuitBreakerMessageSender.class);

    private final MessageSender originalSender;
    private final Message messageToSkip;

    public CircuitBreakerMessageSender(MessageSender originalSender,
                                       Message messageToSkip) {
        this.originalSender = originalSender;
        this.messageToSkip = messageToSkip;
    }

    @Override
    public void sendMessage(Message msg) {
        if (!msg.equals(messageToSkip)) {
            originalSender.sendMessage(msg);
        } else {
            LOG.debug("skip msg to break circuit: {}", msg);
        }
    }

    @Override
    public void flush() {
        originalSender.flush();
    }
}
