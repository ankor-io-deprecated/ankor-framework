package at.irian.ankor.fx.binding.property;

import at.irian.ankor.delay.FloodControl;
import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.listener.RefChangeListener;
import at.irian.ankor.ref.listener.RefListeners;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

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
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        LOG.debug("refreshing observable list {}, new list size is {}", listRef, observableList.size());
                        observableList.callObservers(new BulkUpdateChange<>(observableList));
                    }
                });
//                final ObservableList<T> observableList = getValue();
//                Platform.runLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        LOG.debug("refreshing observable list {}, new list size is {}", listRef, observableList.size());
//                        observableList.callObservers(new BulkUpdateChange<>(observableList));
//                    }
//                });
            }
        });

//
//        floodControl.control(new Runnable() {
//            @Override
//            public void run() {
//
//                DelegateObservableList<T> observableList = (DelegateObservableList<T>) getValue();
//                observableList.callObservers();
//
////                Platform.runLater(new Runnable() {
////                    @Override
////                    public void run() {
////                        DelegateObservableList<T> observableList = (DelegateObservableList<T>) getValue();
////                        observableList.callObservers();
////                        observableList.clear();
////                        Object value = listRef.getValue();
////                        if (value != null) {
////                            if (value instanceof Collection) {
////                                //noinspection unchecked
////                                observableList.addAll((Collection<T>) value);
////                            } else {
////                                LOG.error("Expected value of type Collection, but value of {} is of type {}", listRef, value.getClass().getName());
////                            }
////                        }
////                    }
////                });
//
//            }
//        });
    }

    @Override
    public void set(ObservableList<T> value) {
        super.set(value);
        AnkorPatterns.changeValueLater(listRef, value);
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
