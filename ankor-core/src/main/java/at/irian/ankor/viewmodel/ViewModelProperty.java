package at.irian.ankor.viewmodel;

import at.irian.ankor.base.Wrapper;
import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
@SuppressWarnings("UnusedDeclaration")
public class ViewModelProperty<T> implements Wrapper<T> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ViewModelProperty.class);

    private final Ref ref;
    private T value;

    public ViewModelProperty() {
        this(null);
    }

    public ViewModelProperty(Ref ref) {
        this.ref = ref;
        this.value = null;
    }

    public ViewModelProperty(Ref ref, T initialValue) {
        this.ref = ref;
        this.value = initialValue;
    }

    public ViewModelProperty withRef(Ref ref) {
        return new ViewModelProperty<T>(ref, value);
    }

    public ViewModelProperty withInitialValue(T value) {
        return new ViewModelProperty<T>(ref, value);
    }

    public void set(T newValue) {
        if (ref != null && ref.isValid()) {
            ref.setValue(newValue);
        } else {
            this.value = newValue;
        }
    }

    public T get() {
        return this.value;
    }

    @Override
    public final T getWrappedValue() {
        return this.value;
    }

    @Override
    public final void putWrappedValue(T val) {
        this.value = val;
    }

    protected Ref getRef() {
        return ref;
    }
}
