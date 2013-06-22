package at.irian.ankor.messaging;

/**
 * @author Manfred Geiler
 */
public interface MessageMapper<T> extends MessageSerializer<T>, MessageDeserializer<T> {
}
