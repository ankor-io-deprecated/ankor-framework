package at.irian.ankor.messaging;

/**
 * @author Manfred Geiler
 */
public interface MessageSerializer<T> {

    T serialize(Message msg);

}
