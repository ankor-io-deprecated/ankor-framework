package at.irian.ankor.messaging;

/**
 * @author Thomas Spiegl
 */
@Deprecated
public interface MessageLoop<S> {

    @Deprecated
    MessageBus<S> getMessageBus();

    @Deprecated
    void start(boolean daemon);

    @Deprecated
    void stop();

    @Deprecated
    boolean isConnected();

}
