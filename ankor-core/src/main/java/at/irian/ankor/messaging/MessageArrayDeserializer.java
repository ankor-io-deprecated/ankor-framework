package at.irian.ankor.messaging;

/**
 * @author Manfred Geiler
 */
public interface MessageArrayDeserializer<T> {

    Message[] deserializeArray(T serializedMessages);

}
