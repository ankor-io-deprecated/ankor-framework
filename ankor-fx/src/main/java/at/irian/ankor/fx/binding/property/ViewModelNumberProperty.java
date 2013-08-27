package at.irian.ankor.fx.binding.property;

import at.irian.ankor.ref.Ref;

public class ViewModelNumberProperty<T extends Number> extends ViewModelProperty<Number> {
    public ViewModelNumberProperty(Ref parentObjectRef, String propertyName) {
        super(parentObjectRef, propertyName);
    }
}
