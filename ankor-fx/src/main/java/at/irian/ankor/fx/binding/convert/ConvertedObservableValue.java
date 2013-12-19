package at.irian.ankor.fx.binding.convert;

import at.irian.ankor.converter.Converter;
import com.sun.javafx.binding.ExpressionHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * @author Manfred Geiler
 */
public class ConvertedObservableValue<A,B> implements ObservableValue<B> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ConvertedObservableValue.class);

    protected final ObservableValue<A> observableValue;
    protected final Converter<A,B> converter;
    private ExpressionHelper<B> helper = null;

    public ConvertedObservableValue(ObservableValue<A> observableValue, Converter<A, B> converter) {
        this.observableValue = observableValue;
        this.converter = converter;
        observableValue.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                ExpressionHelper.fireValueChangedEvent(helper);
            }
        });
        observableValue.addListener(new ChangeListener<A>() {
            @Override
            public void changed(ObservableValue<? extends A> observableValue, A oldValue, A newValue) {
                ExpressionHelper.fireValueChangedEvent(helper);
            }
        });
    }

    @Override
    public void addListener(ChangeListener<? super B> changeListener) {
        helper = ExpressionHelper.addListener(helper, this, changeListener);
    }

    @Override
    public void removeListener(ChangeListener<? super B> changeListener) {
        helper = ExpressionHelper.removeListener(helper, changeListener);
    }

    @Override
    public void addListener(InvalidationListener invalidationlistener) {
        helper = ExpressionHelper.addListener(helper, this, invalidationlistener);
    }

    @Override
    public void removeListener(InvalidationListener invalidationlistener) {
        helper = ExpressionHelper.removeListener(helper, invalidationlistener);
    }

    @Override
    public B getValue() {
        return converter.convertTo(observableValue.getValue());
    }

}
