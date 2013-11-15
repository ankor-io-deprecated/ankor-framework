package at.irian.ankor.fx.binding.fxref;

import at.irian.ankor.converter.BidirectionalConverter;
import at.irian.ankor.converter.Converter;
import at.irian.ankor.fx.binding.property.RefProperty;
import at.irian.ankor.ref.Ref;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

/**
 * @author Manfred Geiler
 */
public interface FxRef extends Ref {
    FxRef root();
    FxRef parent();
    FxRef appendPath(String propertyOrSubPath);
    FxRef appendIndex(int index);
    FxRef appendLiteralKey(String literalKey);
    FxRef appendPathKey(String pathKey);
    FxRef $(String propertyOrSubPath);
    FxRef $(int index);
    FxRef ancestor(String ancestorPropertyName);

    FxRefContext context();

    <T> ObservableValue<T> fxObservable();
    <R,T> ObservableValue<T> fxObservable(Converter<R,T> converter);

    <E> ObservableValue<ObservableList<E>> fxObservableList();

    <T> Property<T> fxProperty();
    <R,T> RefProperty<T> fxProperty(BidirectionalConverter<R,T> converter);
}
