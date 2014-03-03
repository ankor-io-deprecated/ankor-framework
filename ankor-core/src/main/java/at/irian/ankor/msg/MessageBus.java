package at.irian.ankor.msg;

/**
 * @author Manfred Geiler
 */
public interface MessageBus {

    /**
     * Register the given MessageListener.
     * @param messageListener MessageListener to register with this MessageBus
     */
    void registerMessageListener(MessageListener messageListener);

    /**
     * Unregister the given MessageListener.
     * @param messageListener MessageListener to unregister from this MessageBus
     */
    void unregisterMessageListener(MessageListener messageListener);

    /**
     * Start dispatching broadcasted messages to registered listeners.
     */
    void start();

    /**
     * Stop dispatching broadcasted messages to registered listeners.
     */
    void stop();

    /**
     * Dispatch the given message to the appropriate listener(s).
     * @param message  a Message
     */
    void broadcast(Message message);

}
