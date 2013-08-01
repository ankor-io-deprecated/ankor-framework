package at.irian.ankor.fx.binding;

import at.irian.ankor.delay.FloodControl;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.listener.RefChangeListener;
import at.irian.ankor.ref.listener.RefListeners;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

import java.util.Collection;
import java.util.List;

/**
* @author Thomas Spiegl
*/
@SuppressWarnings("unchecked")
public class RefPropertyBinding implements RefChangeListener, javafx.beans.value.ChangeListener<Object> {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RefPropertyBinding.class);

    private final Ref valueRef;
    private final Property property;
    private final FloodControl floodControl;

    public RefPropertyBinding(Ref valueRef, Property property) {
        this(valueRef, property, null);
    }

    public RefPropertyBinding(Ref valueRef, Property property, Long floodControlDelay) {
        this.valueRef = valueRef;
        this.property = property;

        setPropertyValue();

        RefListeners.addPropChangeListener(this.valueRef, this);

        this.property.addListener(this);

        if (floodControlDelay != null) {
            this.floodControl = new FloodControl(valueRef, floodControlDelay);
        } else {
            this.floodControl = null;
        }
    }

    /**
     * Process changes from remote server.
     */
    @Override
    public void processChange(Ref changedProperty) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                setPropertyValue();
            }
        });
    }

    private void setPropertyValue() {
        Object remoteValue = valueRef.getValue();
        if (remoteValue instanceof String) {
            property.setValue(remoteValue);
        } else if (remoteValue instanceof Boolean) {
            property.setValue(remoteValue);
        } else if (remoteValue instanceof Enum) {
            property.setValue(((Enum) remoteValue).name());
        } else if (remoteValue instanceof List) {
            Object value = property.getValue();
            if (value instanceof ObservableList) {
                ((ObservableList) value).clear();
                Collection collection = valueRef.getValue();
                if (collection != null) {
                    ((ObservableList) value).addAll(collection);
                }
            } else {
                if (value == null) {
                    ObservableList observableList = new ObservableListWrapper((List) valueRef.getValue());
                    property.setValue(observableList);
                    LOG.warn("Expected observable List found (null)");
                } else {
                    LOG.warn(String.format("Expected observable List found (%s)", value.getClass().getName()));
                }
            }
        } else if (remoteValue != null) {
            property.setValue(remoteValue.toString());
        } else {
            property.setValue(null);
        }
    }

    @SuppressWarnings("TypeParameterExplicitlyExtendsObject")
    @Override
    public void changed(ObservableValue<? extends Object> observableValue, Object oldValue, final Object newValue)  {
        if (floodControl != null) {
            floodControl.control(new Runnable() {
                @Override
                public void run() {
                    valueRef.setValue(newValue);
                }
            });
        } else {
            // we do not have exclusive access to the model here...
            // ... therefore we must not set the Ref value directly:
            valueRef.requestChangeTo(newValue);
        }
    }

}
