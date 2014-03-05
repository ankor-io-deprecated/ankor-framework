package at.irian.ankor.viewmodel.watch;

import at.irian.ankor.change.Change;
import at.irian.ankor.event.source.ModelSource;
import at.irian.ankor.ref.CollectionRef;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.impl.RefImplementor;
import at.irian.ankor.viewmodel.diff.DiffChange;
import at.irian.ankor.viewmodel.diff.ListDiff;

import java.util.Collection;
import java.util.List;

/**
 * @author Manfred Geiler
 */
public class WatchedList<E> extends ExtendedListWrapper<E> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(WatchedList.class);

    private static final int DEFAULT_DIFF_THRESHOLD = 10; // if there are more than 10 diffs then send whole list

    private final CollectionRef listRef;
    private final int diffThreshold;

    public WatchedList(Ref listRef, List<E> wrappedList) {
        this(listRef, wrappedList, DEFAULT_DIFF_THRESHOLD);
    }

    protected WatchedList(Ref listRef, List<E> wrappedList, int diffThreshold) {
        super(wrappedList);
        this.diffThreshold = diffThreshold;
        this.listRef = listRef.toCollectionRef();
    }

    public WatchedList<E> withDiffThreshold(int threshold) {
        return new WatchedList<E>(listRef, wrappedList, threshold);
    }

    @Override
    public E set(int index, E element) {
        E oldElemValue = wrappedList.set(index, element);
        listRef.appendIndex(index).signalValueChange();
        return oldElemValue;
    }

    @Override
    public void add(int index, E element) {
        wrappedList.add(index, element);
        ((RefImplementor)listRef).signal(new ModelSource(listRef, this),
                                         Change.insertChange(index, element));
    }

    @Override
    public E remove(int index) {
        E removedElement = wrappedList.remove(index);
        ((RefImplementor)listRef).signal(new ModelSource(listRef, this),
                                         Change.deleteChange(index));
        return removedElement;
    }


    /**
     * Optimized implementation that directly manipulates the wrapped list and signals one replace change.
     */
    @Override
    public boolean setAll(Collection<? extends E> col) {
        if (col instanceof List) {
            @SuppressWarnings("unchecked") List<E> newList = (List<E>) col;
            List<DiffChange<E>> diffChanges = new ListDiff<E>(wrappedList, newList).withThreshold(diffThreshold).calcDiffChanges();
            wrappedList.clear();
            boolean added = false;
            for (E element : col) {
                added |= wrappedList.add(element);
            }
            if (diffChanges != null) {
                ListDiff.signalChanges(new ModelSource(listRef, this),
                                       listRef, diffChanges);
            } else {
                listRef.signalValueChange();
            }
            return added;
        } else {
            return super.setAll(col);
        }
    }

}
