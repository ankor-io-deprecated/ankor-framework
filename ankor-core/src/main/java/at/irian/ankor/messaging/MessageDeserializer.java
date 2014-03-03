package at.irian.ankor.messaging;

/**
 * @param <T>  type of serialized message
 * @author Manfred Geiler
 */
public interface MessageDeserializer<T> {

    /**
     * @param serializedMsg serialized message
     * @param type type of message
     * @param <M> type of deserialized message
     * @return the deserialied message of type M
     */
    <M> M deserialize(T serializedMsg, Class<M> type);

}
