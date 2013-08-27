package at.irian.ankor.fx.binding.property;

import at.irian.ankor.ref.Ref;

public class ViewModelIntegerProperty extends ViewModelNumberProperty<Integer> {
    public ViewModelIntegerProperty(Ref parentObjectRef, String propertyName) {
        super(parentObjectRef, propertyName);
    }
}
