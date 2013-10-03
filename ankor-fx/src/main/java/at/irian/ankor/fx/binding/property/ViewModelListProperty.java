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
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ViewModelListProperty.class);

    private static final int DEFAULT_FLOOD_CONTROL_DELAY_MILLIS = 5;

    private final FloodControl floodControl;
    private final Ref listRef;

    public ViewModelListProperty(Ref parentObjectRef, String propertyName) {
        this(parentObjectRef, propertyName, DEFAULT_FLOOD_CONTROL_DELAY_MILLIS);
    }

    public ViewModelListProperty(Ref parentObjectRef, String propertyName, int floodControlDelayMillis) {
        super(new ObservableListWrapper<>(new ArrayList<T>()));
        this.listRef = parentObjectRef.appendPath(propertyName);
        this.floodControl = new FloodControl(listRef, floodControlDelayMillis);
        processChange(this.listRef);
        RefListeners.addTreeChangeListener(listRef, this);
    }

    @Override
    public void processChange(final Ref changedProperty) {
        floodControl.control(new Runnable() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        List<T> observableList = getValue();
                        observableList.clear();
                        Object value = listRef.getValue();
                        if (value != null) {
                            if (value instanceof Collection) {
                                //noinspection unchecked
                                observableList.addAll((Collection<T>) value);
                            } else {
                                LOG.error("Expected value of type Collection, but value of {} is of type {}", listRef, value.getClass().getName());
                            }
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
