package at.irian.ankor.fx.binding.convert;

import at.irian.ankor.converter.BidirectionalConverter;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * @author Manfred Geiler
 */
public class ConvertedProperty<A,B> extends ConvertedObservableValue<A,B> implements Property<B> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ConvertedProperty.class);

    private final ChangeListener<B> observableChangeListener;
    private ObservableValue<? extends B> boundObservable = null;

    public ConvertedProperty(Property<A> property, BidirectionalConverter<A, B> converter) {
        super(property, converter);
        this.observableChangeListener = new ChangeListener<B>() {
            @Override
            public void changed(ObservableValue<? extends B> observableValue, B oldValue, B newValue) {
                ConvertedProperty.this.setValue(newValue);
            }
        };
    }


    @Override
    public void setValue(B b) {
        ((Property<A>)observableValue).setValue(((BidirectionalConverter<A,B>)converter).convertFrom(b));
    }

    @Override
    public void bind(ObservableValue<? extends B> observableValue) {
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
    public boolean isBound() {
        return boundObservable != null;
    }

    @Override
    public void bindBidirectional(Property<B> property) {
        Bindings.bindBidirectional(this, property);
    }

    @Override
    public void unbindBidirectional(Property<B> property) {
        Bindings.unbindBidirectional(this, property);
    }

    @Override
    public Object getBean() {
        return ((Property<A>)observableValue).getBean();
    }

    @Override
    public String getName() {
        return ((Property<A>)observableValue).getName();
    }
}
