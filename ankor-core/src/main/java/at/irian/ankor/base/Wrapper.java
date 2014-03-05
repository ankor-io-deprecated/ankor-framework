package at.irian.ankor.base;

/**
 * Marker interface for types that "wrap" some other objects.
 * By means of this interface it is possible to wrap and unwrap objects with a given instance without knowing
 * the actual implementing java type.
 *
 * @author Manfred Geiler
 */
@Deprecated  // todo  still needed?
public interface Wrapper<T> {
    T getWrappedValue();
    void putWrappedValue(T val);
}
