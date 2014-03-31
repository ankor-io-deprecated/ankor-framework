package at.irian.ankor.serialization;

/**
 * @param <T>  type of serialized message
 * @author Manfred Geiler
 */
public interface MessageSerializer<T> {
    T serialize(Object msg);
}
