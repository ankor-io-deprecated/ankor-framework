package at.irian.ankor.sample.fx.binding;

import at.irian.ankor.core.listener.ModelChangeListener;
import at.irian.ankor.core.ref.ModelRef;
import at.irian.ankor.sample.fx.app.App;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * @author Thomas Spiegl
 */
public class ModelBindings {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelBinding.class);

    public static void bind(final ModelRef modelRef, final StringProperty stringProperty, BindingContext context) {

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

    private static void setValue(ModelRef modelRef, SimpleStringProperty prop) {
        String value;
        if (modelRef.getValue() instanceof String) {
            value = modelRef.getValue();
        } else {
            value = modelRef.getValue() != null ? modelRef.getValue().toString() : null;
        }
        prop.setValue(value);
    }

    private static void registerRemoteListener(ModelRef modelRef, final StringProperty stringProperty) {
        App.getApplication().getListenerRegistry().registerRemoteChangeListener(modelRef,
                new ModelChangeListener() {

                    public void handleModelChange(ModelRef watchedRef, ModelRef changedRef) {
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
                        stringProperty.setValue(strValue);
                    }
                });
    }

    private static void registerLocalListener(final ModelRef modelRef, SimpleStringProperty prop) {
        prop.addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                modelRef.setValue(newValue);
            }
        });
    }

}
