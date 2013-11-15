package at.irian.ankor.fx.binding.property;

import at.irian.ankor.ref.Ref;

/**
 * @deprecated  use {@link at.irian.ankor.fx.binding.value.ObservableRef} or {@link RefProperty}
 * @see at.irian.ankor.fx.binding.fxref.FxRefs
 */
@Deprecated
public class ViewModelIntegerProperty extends ViewModelNumberProperty<Integer> {
    public ViewModelIntegerProperty(Ref parentObjectRef, String propertyName) {
        super(parentObjectRef, propertyName);
    }
}
