package at.irian.ankor.fx.binding.property;

import at.irian.ankor.delay.FloodControl;
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
import java.util.List;

public class ViewModelListProperty<T> extends SimpleListProperty<T> implements RefChangeListener {

    private final FloodControl floodControl;
    private final Ref listRef;

    public ViewModelListProperty(Ref parentObjectRef, String propertyName) {
        super(new ObservableListWrapper<>(new ArrayList<T>()));
        this.listRef = parentObjectRef.appendPath(propertyName);
        this.floodControl = new FloodControl(listRef, 5);
        processChange(this.listRef);
        RefListeners.addTreeChangeListener(listRef, this);
    }

    private int count;

    @Override
    public void processChange(final Ref changedProperty) {
        floodControl.control(new Runnable() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        ObservableList<T> observableList = getValue();
                        observableList.clear();
                        Object value = listRef.getValue();
                        if (value instanceof Collection) {
                            //noinspection unchecked
                            observableList.addAll((Collection<T>) value);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void set(ObservableList<T> value) {
        super.set(value);
        AnkorPatterns.changeValueLater(listRef, value);
    }
}
