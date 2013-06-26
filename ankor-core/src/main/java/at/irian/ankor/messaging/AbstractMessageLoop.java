package at.irian.ankor.messaging;

/**
 * @author Manfred Geiler
 */
public abstract class AbstractMessageLoop<S> implements MessageLoop<S> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AbstractMessageLoop.class);

    protected final String name;
    protected final MessageBus<S> messageBus;
    protected final Runnable receiveLoop;
    private Thread receiveLoopThread;

    public AbstractMessageLoop(final String name, MessageMapper<S> messageMapper) {
        this.name = name;
        this.messageBus = new MessageBus<S>(messageMapper) {
            @Override
            protected void sendSerializedMessage(S msg) {
                if (!isConnected()) {
                    throw new IllegalStateException("not connected");
                }
                LOG.debug("{} sends {}", name, msg);
                send(msg);
            }

            @Override
            public void flush() {
            }
        };
        this.receiveLoop = new Runnable() {
            @Override
            public void run() {
                if (!isConnected()) {
                    throw new IllegalStateException("not connected");
                }
                boolean interrupted = false;
                LOG.debug("{} is listening...", name);
                while (!interrupted) {
                    try {
                        S msg = receive();
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

    @Override
    public MessageBus<S> getMessageBus() {
        return messageBus;
    }

    @Override
    public void start() {
        if (receiveLoopThread != null) {
            throw new IllegalStateException("Already started");
        }
        receiveLoopThread = new Thread(receiveLoop);
        receiveLoopThread.setDaemon(true);
        receiveLoopThread.start();
        LOG.info("{} started", this);
    }

    @Override
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

    protected abstract void send(S msg);

    protected abstract S receive() throws InterruptedException;

}
