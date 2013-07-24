package at.irian.ankor.model;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.Wrapper;

/**
 * @author Manfred Geiler
 */
@SuppressWarnings("UnusedDeclaration")
public class ModelProperty<T> implements Wrapper<T> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelProperty.class);

    private final Ref ref;
    private T value;

    ModelProperty() {
        this(null, null);
    }

    ModelProperty(Ref ref) {
        this(ref, null);
    }

    ModelProperty(Ref ref, T initialValue) {
        this.ref = ref;
        this.value = initialValue;
    }


    public static <T> ModelProperty<T> createUnreferencedProperty() {
        return new ModelProperty<T>(null, null);
    }

    public static <T> ModelProperty<T> createUnreferencedProperty(T initialValue) {
        return new ModelProperty<T>(null, initialValue);
    }

    public static <T> ModelProperty<T> createReferencedProperty(Ref ref) {
        return new ModelProperty<T>(ref, null);
    }

    public static <T> ModelProperty<T> createReferencedProperty(Ref ref, T initialValue) {
        return new ModelProperty<T>(ref, initialValue);
    }


    public void set(T newValue) {
        if (this.ref == null) {
            //throw new IllegalStateException("no ref");
            LOG.warn("setting non-referencing object {}", this);
            putWrappedValue(newValue);
        } else {
            // set value by indirection over ankor ref system
            this.ref.setValue(newValue);
        }
    }

    public T get() {
        return getWrappedValue();
    }


    @Override
    public final T getWrappedValue() {
        return this.value;
    }

    @Override
    public final void putWrappedValue(T val) {
        this.value = val;
    }
}
