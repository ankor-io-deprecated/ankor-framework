package at.irian.ankor.fx.binding.value;

import at.irian.ankor.change.Change;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Package local helper class for {@link ObservableListRef}.
 *
 * @author Manfred Geiler
 */
class FxListChangeHelper<E> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(FxListChangeHelper.class);

    private final ObservableList<E> observableList;

    public FxListChangeHelper(ObservableList<E> observableList) {
        this.observableList = observableList;
    }

    public ListChangeListener.Change<E> toFxChange(Change ankorChange) {
        // todo  optimize for different ankor change types
        return new BulkUpdateChange<>(observableList);
    }


    private static class BulkUpdateChange<T> extends ListChangeListener.Change<T> {

        public BulkUpdateChange(ObservableList<T> observableList) {
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
