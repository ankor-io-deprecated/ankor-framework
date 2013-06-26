package at.irian.ankor.messaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Manfred Geiler
 */
public class BufferedMessageSender implements MessageSender {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BufferedMessageSender.class);

    private final MessageSender original;
    private List<Message> messages = null;

    public BufferedMessageSender(MessageSender original) {
        this.original = original;
    }

    @SuppressWarnings("UnusedDeclaration")
    protected MessageSender getOriginalSender() {
        return original;
    }

    protected List<Message> getBufferedMessages() {
        return messages != null ? Collections.unmodifiableList(messages) : Collections.<Message>emptyList();
    }

    @Override
    public void sendMessage(Message msg) {
        if (messages == null) {
            messages = new ArrayList<Message>();
        }
        messages.add(msg);
    }

    @Override
    public void flush() {
        for (Message message : messages) {
            original.sendMessage(message);
        }
    }

}
