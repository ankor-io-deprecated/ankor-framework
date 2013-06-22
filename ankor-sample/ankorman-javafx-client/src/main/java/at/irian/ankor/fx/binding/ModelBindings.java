package at.irian.ankor.fx.binding;

import at.irian.ankor.event.ChangeListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankorman.sample1.fxclient.App;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextInputControl;
import javafx.scene.text.Text;

import java.util.Collection;
import java.util.List;

/**
 * @author Thomas Spiegl
 */
public class ModelBindings {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelBinding.class);

    public static <T> void bind(final Ref valueRef, final Ref itemsRef, final ComboBox<T> comboBox
    ) {
        //noinspection unchecked

        setValue(valueRef, itemsRef, comboBox);

        registerLocalListener(valueRef, comboBox);

        registerRemoteListener(itemsRef, comboBox.itemsProperty());
    }

    public static void bind(final Ref valueRef, final Text text, BindingContext context) {
        bindText(valueRef, text.textProperty(), context);
    }

    public static void bind(final Ref valueRef, final TextInputControl control, BindingContext context) {
        bindText(valueRef, control.textProperty(), context);
    }

    public static void bindText(Ref valueRef, StringProperty stringProperty, BindingContext context) {

        SimpleStringProperty prop = createBinding(stringProperty, context);

        setValue(valueRef, prop);

        registerLocalListener(valueRef, prop);

        registerRemoteListener(valueRef, stringProperty);
    }

    // private

    private static <T> void registerLocalListener(final Ref valueRef, ComboBox<T> comboBox) {
        comboBox.valueProperty().addListener(new javafx.beans.value.ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                valueRef.setValue(newValue);
            }
        });
    }

    private static <T> void setValue(Ref valueRef, Ref itemsRef, ComboBox<T> comboBox) {
        comboBox.getItems().<T>addAll((List<T>) itemsRef.getValue());
        comboBox.valueProperty().setValue((T) valueRef.getValue());
    }

    private static SimpleStringProperty createBinding(StringProperty stringProperty, BindingContext context) {
        SimpleStringProperty prop = new SimpleStringProperty();
        Bindings.bindBidirectional(prop, stringProperty);
        context.add(prop);
        return prop;
    }

    private static void setValue(Ref modelRef, SimpleStringProperty prop) {
        String value;
        if (modelRef.getValue() instanceof String) {
            value = modelRef.getValue();
        } else {
            value = modelRef.getValue() != null ? modelRef.getValue().toString() : null;
        }
        prop.setValue(value);
    }

    private static <T> void registerRemoteListener(Ref modelRef, final ObjectProperty<ObservableList<T>> property) {
        App.application().getListenerRegistry().registerRemoteChangeListener(modelRef,
                new ChangeListener() {

                    public void processChange(Ref modelContext, Ref watchedProperty, Ref changedProperty) {
                        property.getValue().clear();
                        //noinspection unchecked
                        property.getValue().addAll((Collection) watchedProperty.getValue());
                    }
                });
    }

    private static void registerRemoteListener(Ref modelRef, final StringProperty property) {
        App.application().getListenerRegistry().registerRemoteChangeListener(modelRef,
                new ChangeListener() {

                    public void processChange(Ref modelContext, Ref watchedProperty, Ref changedProperty) {
                        String strValue;
                        Object newValue = watchedProperty.getValue();
                        if (newValue instanceof String) {
                            strValue = (String) newValue;
                        } else {
                            if (newValue != null) {
                                strValue = newValue.toString();
                            } else {
                                strValue = null;
                            }
                        }
                        property.setValue(strValue);
                    }
                });
    }

    private static void registerLocalListener(final Ref modelRef, SimpleStringProperty prop) {
        prop.addListener(new javafx.beans.value.ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                try {
                    modelRef.setValue(newValue);
                } catch(IllegalArgumentException ignored) {
                }
            }
        });
    }

}
