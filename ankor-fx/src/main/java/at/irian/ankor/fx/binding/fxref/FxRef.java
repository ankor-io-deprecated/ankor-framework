package at.irian.ankor.fx.binding.fxref;

import at.irian.ankor.ref.Ref;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;

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
    <T> Property<T> fxProperty();
}
