package at.irian.ankor.fx.binding;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.listener.RefListeners;
import javafx.beans.property.Property;

/**
 * @author Florian
 */
public class RefTreeBinding extends RefPropertyBinding {
    public RefTreeBinding(Ref valueRef, Property property) {
        super(valueRef, property);
    }

    public RefTreeBinding(Ref valueRef, Property property, Long floodControlDelay) {
        super(valueRef, property, floodControlDelay);
    }

    @Override
    protected void setChangeListener() {
        RefListeners.addTreeChangeListener(getValueRef(), this);
    }
}
