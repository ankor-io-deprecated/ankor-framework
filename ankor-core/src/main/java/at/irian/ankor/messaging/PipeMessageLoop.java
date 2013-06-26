package at.irian.ankor.messaging;

import java.util.concurrent.BlockingQueue;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class PipeMessageLoop<S> extends AbstractMessageLoop<S> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PipeMessageLoop.class);

    private BlockingQueue<S> sendQueue;
    private BlockingQueue<S> receiveQueue;

    public PipeMessageLoop(final String name, MessageMapper<S> messageMapper) {
        super(name, messageMapper);
    }

    @Override
    protected void send(S msg) {
        try {
            sendQueue.put(msg);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    protected S receive() throws InterruptedException {
        return receiveQueue.take();
    }

    public void setSendQueue(BlockingQueue<S> sendQueue) {
        this.sendQueue = sendQueue;
    }

    public void setReceiveQueue(BlockingQueue<S> receiveQueue) {
        this.receiveQueue = receiveQueue;
    }

    @Override
    public boolean isConnected() {
        return sendQueue != null && receiveQueue != null;
    }

}
