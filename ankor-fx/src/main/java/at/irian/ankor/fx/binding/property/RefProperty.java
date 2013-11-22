package at.irian.ankor.fx.binding.property;

import at.irian.ankor.change.Change;
import at.irian.ankor.event.source.CustomSource;
import at.irian.ankor.fx.binding.value.ObservableRef;
import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.impl.RefImplementor;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * @author Manfred Geiler
 */
public class RefProperty<T> extends ObservableRef<T> implements Property<T> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RefProperty.class);

    private final ChangeListener<T> observableChangeListener;

    private ObservableValue<? extends T> observable = null;

    public RefProperty(Ref ref) {
        this(ref, null);
    }

    public RefProperty(Ref ref, T defaultValue) {
        super(ref, defaultValue);
        this.observableChangeListener = new ChangeListener<T>() {
            @Override
            public void changed(ObservableValue<? extends T> observableValue, T oldValue, T newValue) {
                setRefValue(newValue);
            }
        };
    }

    @Override
    public void bind(ObservableValue<? extends T> observableValue) {
        if (observableValue == null) {
            throw new NullPointerException("Cannot bind to null");
        }
        if (!observableValue.equals(this.observable)) {
            unbind();
            setValue(observableValue.getValue());
            this.observable = observableValue;
            this.observable.addListener(observableChangeListener);
        }
    }

    public void unbind() {
        if (observable != null) {
            observable.removeListener(observableChangeListener);
            observable = null;
        }
    }

    @Override
    public void setValue(T newValue) {
        if (isBound()) {
            throw new RuntimeException("A bound value cannot be set.");
        }
        setRefValue(newValue);
    }

    protected void setRefValue(final Object newValue) {
        AnkorPatterns.runLater(this.ref, new Runnable() {
            @Override
            public void run() {
                ((RefImplementor)RefProperty.this.ref).apply(new CustomSource(RefProperty.this),
                                                             Change.valueChange(newValue));
            }
        });
    }

    @Override
    public void bindBidirectional(Property<T> property) {
        Bindings.bindBidirectional(this, property);
    }

    @Override
    public void unbindBidirectional(Property<T> property) {
        Bindings.unbindBidirectional(this, property);
    }

    public boolean isBound() {
        return observable != null;
    }

    public Object getBean() {
        return ref.isRoot() ? null : ref.parent().getValue();
    }

    public String getName() {
        return ref.propertyName();
    }

}
