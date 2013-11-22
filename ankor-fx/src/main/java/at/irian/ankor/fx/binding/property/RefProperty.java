package at.irian.ankor.fx.binding.property;

import at.irian.ankor.change.Change;
import at.irian.ankor.event.source.CustomSource;
import at.irian.ankor.fx.binding.cache.FxCacheSupport;
import at.irian.ankor.fx.binding.value.ObservableRef;
import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.impl.RefImplementor;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.util.Callback;

/**
 * @author Manfred Geiler
 */
public class RefProperty<T> extends ObservableRef<T> implements Property<T> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RefProperty.class);

    private final ChangeListener<T> observableChangeListener;
    private ObservableValue<? extends T> boundObservable = null;

    protected RefProperty(Ref ref, T defaultValue) {
        super(ref, defaultValue);
        this.observableChangeListener = new ChangeListener<T>() {
            @Override
            public void changed(ObservableValue<? extends T> observableValue, T oldValue, T newValue) {
                setRefValue(newValue);
            }
        };
    }


    public static <T> Property<T> createProperty(Ref ref) {
        return createProperty(ref, null);
    }

    public static <T> Property<T> createProperty(Ref ref, final T defaultValue) {
        return FxCacheSupport.getBindingCache(ref).getProperty(ref, defaultValue, new Callback<Ref, Property<T>>() {
            @Override
            public Property<T> call(Ref ref) {
                LOG.debug("Creating Property for {}", ref);
                return new RefProperty<>(ref, defaultValue);
            }
        });
    }


    @Override
    public void bind(ObservableValue<? extends T> observableValue) {
        if (observableValue == null) {
            throw new NullPointerException("Cannot bind to null");
        }
        if (!observableValue.equals(this.boundObservable)) {
            unbind();
            setValue(observableValue.getValue());
            this.boundObservable = observableValue;
            this.boundObservable.addListener(observableChangeListener);
        }
    }

    @Override
    public void unbind() {
        if (boundObservable != null) {
            boundObservable.removeListener(observableChangeListener);
            boundObservable = null;
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
        return boundObservable != null;
    }

    public Object getBean() {
        return ref.isRoot() ? null : ref.parent().getValue();
    }

    public String getName() {
        return ref.propertyName();
    }

}
