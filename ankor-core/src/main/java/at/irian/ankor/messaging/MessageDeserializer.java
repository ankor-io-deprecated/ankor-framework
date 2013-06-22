package at.irian.ankor.messaging;

/**
 * @author Manfred Geiler
 */
public interface MessageDeserializer<T> {

    Message deserialize(T serializedMsg);

}
