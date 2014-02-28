package at.irian.ankor.messaging;

import at.irian.ankor.connection.RemoteSystem;

import java.util.Collection;

/**
 * @author Manfred Geiler
 */
public abstract class AbstractMessageLoop<S> implements MessageLoop<S> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AbstractMessageLoop.class);

    protected final String name;
    protected final MessageBus<S> messageBus;
    protected final Runnable receiveLoop;
    private Thread receiveLoopThread;

    public AbstractMessageLoop(final String systemName, MessageMapper<S> messageMapper) {
        this.name = systemName;
        this.messageBus = new MessageBus<S>(messageMapper) {
            @Override
            protected void sendSerializedMessage(String remoteSystemId, S msg) {
                if (!isConnected()) {
                    throw new IllegalStateException("not connected");
                }
                LOG.debug("{} sends {} to {}", systemName, msg, remoteSystemId);
                send(remoteSystemId, msg);
            }

            @Override
            public Collection<? extends RemoteSystem> getKnownRemoteSystems() {
                return AbstractMessageLoop.this.getKnownRemoteSystems();
            }
        };
        this.receiveLoop = new Runnable() {
            @Override
            public void run() {
                if (!isConnected()) {
                    throw new IllegalStateException("not connected");
                }
                boolean interrupted = false;
                LOG.debug("{} is listening...", systemName);
                while (!interrupted) {
                    try {
                        S msg = receive();
                        LOG.trace("{} receives {}", systemName, msg);
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

    protected abstract Collection<? extends RemoteSystem> getKnownRemoteSystems();

    @Override
    public void start(boolean daemon) {
        if (receiveLoopThread != null) {
            throw new IllegalStateException("Already started");
        }
        receiveLoopThread = new Thread(receiveLoop, "Ankor '" + name + "'");
        receiveLoopThread.setDaemon(daemon);
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

    protected abstract void send(String remoteSystemId, S msg);

    protected abstract S receive() throws InterruptedException;

}
