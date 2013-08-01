package at.irian.ankor.messaging;

/**
 * @author Thomas Spiegl
 */
public interface MessageLoop<S> {

    MessageBus<S> getMessageBus();

    void start(boolean daemon);

    void stop();

    boolean isConnected();

}
