package at.irian.ankor.msg;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple MessageDispatcher.
 * Please note: registering/unregistering of listeners are thread-<strong>unsafe</strong> operations in this implementation!
 *
 * todo  delegate message.processBy to workers
 *
 * @author Manfred Geiler
 */
public class SimpleMessageBus implements MessageBus {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleMessageBus.class);

    protected final List<MessageListener> messageListeners = new ArrayList<MessageListener>();
    private volatile boolean started = false;

    @Override
    public void registerMessageListener(MessageListener messageListener) {
        messageListeners.add(messageListener);
    }

    @Override
    public void unregisterMessageListener(MessageListener messageListener) {
        messageListeners.remove(messageListener);
    }

    @Override
    public void start() {
        this.started = true;
    }

    @Override
    public void stop() {
        this.started = false;
    }

    @Override
    public void broadcast(Message message) {
        if (!started) {
            throw new IllegalStateException("MessageBus was stopped or is not yet started");
        }

        LOG.debug("Broadcasting message {}", message);

        boolean handled = false;
        for (MessageListener messageListener : messageListeners) {
            if (message.isAppropriateListener(messageListener)) {
                handled = true;
                try {
                    message.processBy(messageListener);
                } catch (Exception e) {
                    LOG.error("MessageListener " + messageListener + " failed on message " + message, e);
                }
            }
        }
        if (!handled) {
            LOG.error("Unhandled message of type " + message.getClass());
        }
    }
}
