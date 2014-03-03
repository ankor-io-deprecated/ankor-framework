package at.irian.ankor.messaging;

/**
 * A MessageMapper is responsible of serializing and deserializing messages for the transport layer.
 * @param <T>  type of serialized message
 * @author Manfred Geiler
 */
public interface MessageMapper<T> extends MessageSerializer<T>, MessageDeserializer<T> {
}
