package at.irian.ankor.fx.binding.property;

import at.irian.ankor.delay.FloodControl;
import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.listener.RefChangeListener;
import at.irian.ankor.ref.listener.RefListeners;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * @deprecated  use {@link at.irian.ankor.fx.binding.value.ObservableRef} or {@link RefProperty}
 * @see at.irian.ankor.fx.binding.fxref.FxRefs
 */
@Deprecated
public class ViewModelListProperty<T> extends SimpleListProperty<T> implements RefChangeListener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ViewModelListProperty.class);

    private static final int DEFAULT_FLOOD_CONTROL_DELAY_MILLIS = 5;

    private final FloodControl floodControl;
    private final Ref listRef;

    public ViewModelListProperty(Ref parentObjectRef, String propertyName) {
        this(parentObjectRef, propertyName, DEFAULT_FLOOD_CONTROL_DELAY_MILLIS);
    }

    public ViewModelListProperty(Ref parentObjectRef, String propertyName, int floodControlDelayMillis) {
        super(ViewModelListProperty.<T>createObservableList(parentObjectRef, propertyName));
        this.listRef = parentObjectRef.appendPath(propertyName);
        this.floodControl = new FloodControl(listRef, floodControlDelayMillis);
        processChange(this.listRef);
        RefListeners.addTreeChangeListener(listRef, this);
    }

    private static <T> ObservableList<T> createObservableList(Ref parentObjectRef, String propertyName) {
        return new DelegateObservableList<>(new ListRefDelegateList<T>(parentObjectRef.appendPath(propertyName)));
    }

    @Override
    public void processChange(final Ref changedProperty) {
        floodControl.control(new Runnable() {
            @Override
            public void run() {
                final DelegateObservableList<T> observableList = (DelegateObservableList<T>) getValue();
                LOG.debug("refreshing observable list {} with size {}", listRef, observableList.size());
                observableList.callObservers(new BulkUpdateChange<>(observableList));
            }
        });
    }

    @Override
    public void set(ObservableList<T> value) {
        super.set(value);
        AnkorPatterns.changeValueLater(listRef, value); // we must not directly access the model context from a non-dispatching thread
    }

    private static class BulkUpdateChange<T> extends ListChangeListener.Change<T> {

        public BulkUpdateChange(DelegateObservableList<T> observableList) {
            super(observableList);
        }

        @Override
        public boolean next() {
            return false;
        }

        @Override
        public void reset() {
        }

        @Override
        public int getFrom() {
            return 0;
        }

        @Override
        public int getTo() {
            return getList().size();
        }

        @Override
        public List<T> getRemoved() {
            return null;
        }

        @Override
        protected int[] getPermutation() {
            return null;
        }

        @Override
        public boolean wasUpdated() {
            return true;
        }
    }
}
