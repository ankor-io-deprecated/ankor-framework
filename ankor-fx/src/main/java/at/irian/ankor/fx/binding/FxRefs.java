package at.irian.ankor.fx.binding;

import at.irian.ankor.fx.binding.property.RefProperty;
import at.irian.ankor.fx.binding.value.ObservableRef;
import at.irian.ankor.ref.Ref;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;

/**
 * @author Manfred Geiler
 */
@SuppressWarnings("UnusedDeclaration")
public final class FxRefs {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(FxRefs.class);

    private FxRefs() {}

    public static <T> ObservableValue<T> observable(Ref ref)  {
        return new ObservableRef<>(ref);
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


    public static <T> Property<T> property(Ref ref)  {
        return new RefProperty<>(ref);
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
