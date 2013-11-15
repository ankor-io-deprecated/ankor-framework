package at.irian.ankor.fx.binding.property;

import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.listener.RefChangeListener;
import at.irian.ankor.ref.listener.RefListeners;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @deprecated  use {@link at.irian.ankor.fx.binding.value.ObservableRef} or {@link RefProperty}
 * @see at.irian.ankor.fx.binding.FxRefs
 */
@Deprecated
public class ViewModelProperty<T> extends SimpleObjectProperty<T> implements RefChangeListener {

    private Ref ref;
    private volatile boolean fxChange;

    public ViewModelProperty(Ref parentObjectRef, String propertyName) {
        super(parentObjectRef.appendPath(propertyName).<T>getValue());
        this.ref = parentObjectRef.appendPath(propertyName);

        RefListeners.addPropChangeListener(ref, this);
    }

    @Override
    public void processChange(final Ref changedProperty) {
        if (fxChange) {
            fxChange = false;
            return;
        }
        super.set(ref.<T>getValue());
    }

    @Override
    public void set(T v) {
        fxChange = true;
        super.set(v);
        AnkorPatterns.changeValueLater(ref, v);  // we must not directly access the model context from a non-dispatching thread
    }
}
