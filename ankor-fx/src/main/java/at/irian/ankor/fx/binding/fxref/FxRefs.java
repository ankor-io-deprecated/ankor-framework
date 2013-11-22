package at.irian.ankor.fx.binding.fxref;

import at.irian.ankor.base.ObjectUtils;
import at.irian.ankor.change.Change;
import at.irian.ankor.change.ChangeEvent;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.converter.BidirectionalConverter;
import at.irian.ankor.converter.Converter;
import at.irian.ankor.event.source.CustomSource;
import at.irian.ankor.fx.binding.property.RefProperty;
import at.irian.ankor.fx.binding.value.ObservableRef;
import at.irian.ankor.fx.binding.value.ObservableValueListRef;
import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.impl.RefImplementor;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

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


    /**
     * Create a bidirectional binding between a Ref and a FX ToggleGroup.
     * The user data of every Toggle in this group must have a valid value compatible to the Ref's java type.
     * @param toggleGroup  toggle group to bind
     * @param ref          Ref to bind
     */
    public static void bindToggleGroup(final ToggleGroup toggleGroup, final Ref ref) {
        final Object circuitBreaker = new Object();
        toggleGroup.selectedToggleProperty().addListener(new javafx.beans.value.ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observableValue, Toggle oldToggle, final Toggle newToggle) {
                if (newToggle != null) {
                    AnkorPatterns.runLater(ref, new Runnable() {
                        @Override
                        public void run() {
                            ((RefImplementor) ref).apply(new CustomSource(circuitBreaker),
                                                         Change.valueChange(newToggle.getUserData()));
                        }
                    });
                }
            }
        });
        ref.context().modelContext().getEventListeners().add(new ChangeEventListener(ref) {
            @Override
            public void process(ChangeEvent event) {
                if (event.getChangedProperty().equals(getWatchedProperty())) {
                    if (event.getSource() instanceof CustomSource
                        && ((CustomSource)event.getSource()).getCustomSourceObject() == circuitBreaker) {
                        // ignore
                        return;
                    }
                    toggleToNewValue(toggleGroup, event.getChange().getValue());
                }
            }
        });
        toggleToNewValue(toggleGroup, ref.getValue());
    }

    private static void toggleToNewValue(ToggleGroup toggleGroup, Object newValue) {
        for (Toggle toggle : toggleGroup.getToggles()) {
            if (ObjectUtils.nullSafeEquals(toggle.getUserData(), newValue)) {
                if (!toggle.isSelected()) {
                    toggleGroup.selectToggle(toggle);
                }
                break;
            }
        }
    }

}
