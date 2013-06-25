package at.irian.ankor.messaging;

import java.util.concurrent.BlockingQueue;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class MessageLoop<S> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MessageLoop.class);

    private final String name;
    private final MessageBus<S> messageBus;
    private final Runnable receiveLoop;

    private BlockingQueue<S> sendQueue;
    private BlockingQueue<S> receiveQueue;
    private Thread receiveLoopThread;

    public MessageLoop(final String name, MessageMapper<S> messageMapper) {
        this.name = name;
        this.messageBus = new MessageBus<S>(messageMapper) {
            @Override
            protected void sendSerializedMessage(S msg) {
                if (!isConnected()) {
                    throw new IllegalStateException("not connected");
                }
                LOG.debug("{} sends {}", name, msg);
                try {
                    sendQueue.put(msg);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        this.receiveLoop = new Runnable() {
            @Override
            public void run() {
                if (!isConnected()) {
                    throw new IllegalStateException("not connected");
                }
                boolean interrupted = false;
                LOG.debug("{} is listening on queue {} ...", MessageLoop.this, receiveQueue.hashCode());
                while (!interrupted) {
                    try {
                        S msg = receiveQueue.take();
                        LOG.debug("{} receives {}", name, msg);
                        messageBus.receiveSerializedMessage(msg);
                    } catch (InterruptedException e) {
                        interrupted = true;
                    }
                }
                Thread.currentThread().interrupt();
            }
        };
    }

    public MessageBus<S> getMessageBus() {
        return messageBus;
    }

    public void setSendQueue(BlockingQueue<S> sendQueue) {
        this.sendQueue = sendQueue;
    }

    public void setReceiveQueue(BlockingQueue<S> receiveQueue) {
        this.receiveQueue = receiveQueue;
    }

    public boolean isConnected() {
        return sendQueue != null && receiveQueue != null;
    }

    public void start() {
        if (receiveLoopThread != null) {
            throw new IllegalStateException("Already started");
        }
        receiveLoopThread = new Thread(receiveLoop);
        receiveLoopThread.setDaemon(true);
        receiveLoopThread.start();
        LOG.info("{} started", this);
    }

    public void stop() {
        if (receiveLoopThread != null) {
            receiveLoopThread.interrupt();
            receiveLoopThread = null;
            LOG.info("{} stopped", this);
        } else {
            throw new IllegalStateException("Not yet started");
        }
    }

    @Override
    public String toString() {
        return "MessageLoop{'" + name + "'}";
    }
}
