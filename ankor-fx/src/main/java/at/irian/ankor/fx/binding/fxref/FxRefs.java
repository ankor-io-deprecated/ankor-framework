package at.irian.ankor.fx.binding.fxref;

import at.irian.ankor.converter.BidirectionalConverter;
import at.irian.ankor.converter.Converter;
import at.irian.ankor.fx.binding.property.RefProperty;
import at.irian.ankor.fx.binding.value.ObservableRef;
import at.irian.ankor.fx.binding.value.ObservableValueListRef;
import at.irian.ankor.ref.Ref;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

/**
 * Convenient methods for obtaining an JavaFX Observable or Property directly from an Ankor Ref.
 *
 * @author Manfred Geiler
 */
@SuppressWarnings("UnusedDeclaration")
public final class FxRefs {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(FxRefs.class);

    private FxRefs() {}

    public static <T> ObservableValue<T> observable(Ref ref)  {
        return new ObservableRef<>(ref);
    }

    public static <R,T> ObservableValue<T> observable(Ref ref, final Converter<R,T> converter)  {
        return new ObservableRef<T>(ref) {
            @Override
            public T getValue() {
                return converter.convertTo((R) ref.getValue());
            }
        };
    }

    public static ObservableValue<String> observableString(Ref ref)  {
        return new ObservableRef<>(ref);
    }

    public static ObservableValue<Integer> observableInteger(Ref ref)  {
        return new ObservableRef<>(ref);
    }

    public static ObservableValue<Enum> observableEnum(Ref ref)  {
        return new ObservableRef<>(ref);
    }

    public static ObservableValue<Boolean> observableBoolean(Ref ref)  {
        return new ObservableRef<>(ref);
    }

    public static <E> ObservableValue<ObservableList<E>> observableList(Ref ref)  {
        return new ObservableValueListRef<>(ref);
    }


    public static <T> Property<T> property(Ref ref)  {
        return new RefProperty<>(ref);
    }

    public static <T> RefProperty<T> property(Ref ref, final BidirectionalConverter<Object, T> converter) {
        return new RefProperty<T>(ref) {
            @Override
            public T getValue() {
                return converter.convertTo(super.getValue());
            }

            protected void setRefValue(Object newValue) {
                //noinspection unchecked
                super.setRefValue(converter.convertFrom((T)newValue));
            }
        };
    }

    public static Property<String> stringProperty(Ref ref)  {
        return new RefProperty<>(ref);
    }

    public static Property<Integer> integerProperty(Ref ref)  {
        return new RefProperty<>(ref);
    }

    public static Property<Enum> enumProperty(Ref ref)  {
        return new RefProperty<>(ref);
    }

}
