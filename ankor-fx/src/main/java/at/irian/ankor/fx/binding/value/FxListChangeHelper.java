package at.irian.ankor.fx.binding.value;

import at.irian.ankor.change.Change;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.Collection;
import java.util.Collections;
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

    @SuppressWarnings("unchecked")
    public ListChangeListener.Change<E> toFxChange(Change ankorChange) {
        switch (ankorChange.getType()) {
            case delete:
                return new DeleteChange(observableList, (Integer)ankorChange.getKey());
            case insert:
                return new InsertChange(observableList, (Integer)ankorChange.getKey(), (E)ankorChange.getValue());
            case replace:
                return new ReplaceChange(observableList, (Integer)ankorChange.getKey(), (Collection<E>)ankorChange.getValue());
            case value:
            default:
                throw new IllegalArgumentException("Unexpected change " + ankorChange);
        }
        //return new BulkUpdateChange<>(observableList);
    }


//    private static class BulkUpdateChange<T> extends ListChangeListener.Change<T> {
//
//        public BulkUpdateChange(ObservableList<T> observableList) {
//            super(observableList);
//        }
//
//        @Override
//        public boolean next() {
//            return false;
//        }
//
//        @Override
//        public void reset() {
//        }
//
//        @Override
//        public int getFrom() {
//            return 0;
//        }
//
//        @Override
//        public int getTo() {
//            return getList().size();
//        }
//
//        @Override
//        public List<T> getRemoved() {
//            return null;
//        }
//
//        @Override
//        protected int[] getPermutation() {
//            return null;
//        }
//
//        @Override
//        public boolean wasUpdated() {
//            return true;
//        }
//    }


    private class DeleteChange extends ListChangeListener.Change<E> {

        private final int idx;

        public DeleteChange(ObservableList<E> observableList, Integer idx) {
            super(observableList);
            this.idx = idx;
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
            return idx;
        }

        @Override
        public int getTo() {
            return idx;
        }

        @Override
        public List<E> getRemoved() {
            return Collections.singletonList(null);  // we have no removed value ...
        }

        @Override
        protected int[] getPermutation() {
            return null;
        }

        @Override
        public boolean wasRemoved() {
            return true;
        }

        @Override
        public int getRemovedSize() {
            return 1;
        }
    }

    private class InsertChange extends ListChangeListener.Change<E> {

        private final int idx;
        private final E value;

        public InsertChange(ObservableList<E> observableList,
                            Integer idx, E value) {
            super(observableList);
            this.idx = idx;
            this.value = value;
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
            return idx;
        }

        @Override
        public int getTo() {
            return idx + 1;
        }

        @Override
        public List<E> getRemoved() {
            return null;
        }

        @Override
        protected int[] getPermutation() {
            return null;
        }

        @Override
        public boolean wasAdded() {
            return true;
        }

        @Override
        public List<E> getAddedSubList() {
            return Collections.singletonList(value);
        }

        @Override
        public int getAddedSize() {
            return 1;
        }
    }

    private class ReplaceChange extends ListChangeListener.Change<E> {

        private final Integer idx;
        private final Collection<E> values;

        public ReplaceChange(ObservableList<E> observableList,
                             Integer idx,
                             Collection<E> values) {
            super(observableList);
            this.idx = idx;
            this.values = values;
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
            return idx;
        }

        @Override
        public int getTo() {
            return idx + values.size();
        }

        @Override
        public List<E> getRemoved() {
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
