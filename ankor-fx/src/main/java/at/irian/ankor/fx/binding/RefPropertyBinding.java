package at.irian.ankor.fx.binding;

import at.irian.ankor.ref.ChangeListener;
import at.irian.ankor.ref.Ref;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

import java.util.Collection;
import java.util.List;

import static at.irian.ankor.util.ObjectUtils.nullSafeEquals;

/**
* @author Thomas Spiegl
*/
@SuppressWarnings("unchecked")
public class RefPropertyBinding implements ChangeListener, javafx.beans.value.ChangeListener<Object> {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RefPropertyBinding.class);

    private final Ref valueRef;
    private final Property property;

    public RefPropertyBinding(Ref valueRef, Property property) {
        this.valueRef = valueRef;
        this.property = property;

        setPropertyValue(valueRef);

        this.valueRef.addPropChangeListener(this);
        this.property.addListener(this);
    }

    @Override
    public void processChange(final Ref watchedProperty, Ref changedProperty) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                setPropertyValue(watchedProperty);
            }
        });
    }

    private void setPropertyValue(Ref valueRef) {
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
    public void changed(ObservableValue<? extends Object> observableValue, Object oldValue, Object newValue)  {
        setRefValue(newValue);
    }

    private void setRefValue(Object newValue) {
        try {
            if (!nullSafeEquals(valueRef.getValue(), newValue)) {
                valueRef.setValue(newValue);
            }
        } catch(IllegalArgumentException ignored) {
        }
    }

}
