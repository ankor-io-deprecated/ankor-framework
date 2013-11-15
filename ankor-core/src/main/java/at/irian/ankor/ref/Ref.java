package at.irian.ankor.ref;

/**
 * Untyped reference to a view model object.
 *
 * @author Manfred Geiler
 */
@SuppressWarnings("UnusedDeclaration")
public interface Ref extends TypedRef<Object> {

    /**
     * Get the value of the underlying model object.
     * @param <T>  type of the model field (for convenience only, so that there is no explicit cast needed)
     * @return value of the underlying model field
     * @throws IllegalStateException if value is not valid
     */
    <T> T getValue();

    <T> TypedRef<T> toTypedRef();
}
