package at.irian.ankor.fx.binding;

import at.irian.ankor.change.ChangeListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.sample.fx.App;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

/**
 * @author Thomas Spiegl
 */
public class ModelBindings {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelBinding.class);

    public static void bind(final Ref modelRef, final StringProperty stringProperty, BindingContext context) {

        SimpleStringProperty prop = createBinding(stringProperty, context);

        setValue(modelRef, prop);

        registerLocalListener(modelRef, prop);

        registerRemoteListener(modelRef, stringProperty);

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

    private static void registerRemoteListener(Ref modelRef, final StringProperty stringProperty) {
        App.application().getListenerRegistry().registerRemoteChangeListener(modelRef,
                new ChangeListener() {

                    public void processChange(Ref contextRef, Ref watchedRef, Ref changedRef) {
                        String strValue;
                        Object newValue = watchedRef.getValue();
                        if (newValue instanceof String) {
                            strValue = (String) newValue;
                        } else {
                            if (newValue != null) {
                                strValue = newValue.toString();
                            } else {
                                strValue = null;
                            }
                        }
                        stringProperty.setValue(strValue); //todo: prevent setting back to model immediately (and sending change back to server)
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
