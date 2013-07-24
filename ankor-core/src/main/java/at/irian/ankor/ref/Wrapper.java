package at.irian.ankor.ref;

/**
 * @author Manfred Geiler
 */
public interface Wrapper<T> {
    T getWrappedValue();
    void putWrappedValue(T val);
}
