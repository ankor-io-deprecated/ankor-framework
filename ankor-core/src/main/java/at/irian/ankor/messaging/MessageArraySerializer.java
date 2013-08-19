package at.irian.ankor.messaging;

/**
 * @author Manfred Geiler
 */
public interface MessageArraySerializer<T> {
    T serializeArray(Message[] messages);
}
