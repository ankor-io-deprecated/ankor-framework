package at.irian.ankor.fx.binding.property;

import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.listener.RefChangeListener;
import at.irian.ankor.ref.listener.RefListeners;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Collection;

public class ViewModelListProperty<T> extends SimpleListProperty<T> implements RefChangeListener {

    private Ref ref;

    public ViewModelListProperty(Ref parentObjectRef, String propertyName) {
        super(new ObservableListWrapper<>(new ArrayList<T>()));
        this.ref = parentObjectRef.appendPath(propertyName);
        processChange(this.ref);
        RefListeners.addPropChangeListener(ref, this);
    }

    @Override
    public void processChange(final Ref changedProperty) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ObservableList<T> observableList = getValue();
                observableList.clear();
                Object value = ref.getValue();
                if (value instanceof Collection) {
                    //noinspection unchecked
                    observableList.addAll((Collection<T>) value);
                }
            }
        });
    }

    @Override
    public void set(ObservableList<T> value) {
        super.set(value);
        AnkorPatterns.changeValueLater(ref, value);
    }
}
