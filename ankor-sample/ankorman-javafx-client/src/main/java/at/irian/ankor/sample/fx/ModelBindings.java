package at.irian.ankor.sample.fx;

import at.irian.ankor.core.listener.ModelChangeListener;
import at.irian.ankor.core.ref.ModelRef;
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
        SimpleStringProperty prop = new SimpleStringProperty();
        prop.setValue((String) modelRef.getValue());
        prop.addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                if (modelRef.getValue() == null && (newValue == null || newValue.equals(""))) {
                    return;
                }
                //modelRef.setValue(newValue);
            }
        });
        Bindings.bindBidirectional(prop, stringProperty);
        context.add(prop);
        Main.clientApp.getListenerRegistry().registerRemoteChangeListener(modelRef,
                new ModelChangeListener() {
                    public void beforeModelChange(ModelRef modelRef, Object oldValue, Object newValue) {
                    }

                    public void afterModelChange(ModelRef modelRef, Object oldValue, Object newValue) {
                        //TestModel testModel = modelRef.getValue();
                        if (newValue instanceof Enum) {
                            newValue = ((Enum) newValue).name();
                        }
                        stringProperty.setValue((String) newValue);
                    }
                });

    }

}
