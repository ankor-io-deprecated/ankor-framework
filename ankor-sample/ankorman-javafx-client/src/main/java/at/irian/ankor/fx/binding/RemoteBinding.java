package at.irian.ankor.fx.binding;

import at.irian.ankor.event.ChangeListener;
import at.irian.ankor.ref.Ref;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

import java.util.Collection;
import java.util.List;

/**
* @author Thomas Spiegl
*/
@SuppressWarnings("unchecked")
public class RemoteBinding implements ChangeListener, javafx.beans.value.ChangeListener<Object> {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RemoteBinding.class);

    private final Ref valueRef;
    private final Property property;

    private Object currentRemoteValue;

    public RemoteBinding(Ref valueRef, Property property) {
        this.valueRef = valueRef;
        this.property = property;

        setRemoteValue(valueRef);

        this.valueRef.registerRemoteChangeListener(this);
        this.property.addListener(this);
    }

    @Override
    public void processChange(Ref modelContext, Ref watchedProperty, Ref changedProperty) {
        setRemoteValue(watchedProperty);
    }

    private void setRemoteValue(Ref valueRef) {
        Object remoteValue = valueRef.getValue();
        if (remoteValue instanceof String) {
            property.setValue(remoteValue);
            currentRemoteValue = remoteValue;
        } else if (remoteValue instanceof Enum) {
            property.setValue(((Enum) remoteValue).name());
            currentRemoteValue = ((Enum) remoteValue).name();
        } else if (remoteValue instanceof List) {
            Object value = property.getValue();
            if (value instanceof ObservableList) {
                ((ObservableList) value).clear();
                Collection collection = valueRef.getValue();
                ((ObservableList) value).addAll(collection);
                currentRemoteValue = collection;
            } else {
                if (value == null) {
                    ObservableList observableList = new ObservableListWrapper((List) valueRef.getValue());
                    property.setValue(observableList);
                    currentRemoteValue = observableList;
                    LOG.warn("Expected observable List found (null)");
                } else {
                    LOG.warn(String.format("Expected observable List found (%s)", value.getClass().getName()));
                }
            }
        } else if (remoteValue != null) {
            property.setValue(remoteValue.toString());
            currentRemoteValue = remoteValue.toString();
        } else {
            property.setValue(null);
            currentRemoteValue = null;
        }
    }

    @SuppressWarnings("TypeParameterExplicitlyExtendsObject")
    @Override
    public void changed(ObservableValue<? extends Object> observableValue, Object oldValue, Object newValue)  {
        try {
            if (!isEqual(currentRemoteValue, newValue)) {
                currentRemoteValue = newValue;
                valueRef.setValue(newValue);
            }
        } catch(IllegalArgumentException ignored) {
        }

    }

    public static boolean isEqual(Object currentValue, Object newValue) {
        return currentValue == null && newValue == null ||
                (currentValue != null && newValue != null && currentValue.equals(newValue));
    }
}
