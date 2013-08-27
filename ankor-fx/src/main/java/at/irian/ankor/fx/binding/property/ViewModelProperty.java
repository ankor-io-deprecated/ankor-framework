package at.irian.ankor.fx.binding.property;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.listener.RefChangeListener;
import at.irian.ankor.ref.listener.RefListeners;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;

public class ViewModelProperty<T> extends SimpleObjectProperty<T> implements RefChangeListener {

    private Ref ref;

    public ViewModelProperty(Ref parentObjectRef, String propertyName) {
        super(parentObjectRef.append(propertyName).<T>getValue());
        this.ref = parentObjectRef.append(propertyName);

        RefListeners.addPropChangeListener(ref, this);
    }

    @Override
    public void processChange(final Ref changedProperty) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ViewModelProperty.super.set(changedProperty.<T>getValue());

            }
        });
    }

    @Override
    public void set(T v) {
        ref.requestChangeTo(v);
    }
}
