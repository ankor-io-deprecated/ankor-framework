package at.irian.ankor.viewmodel.diff;

import at.irian.ankor.change.Change;
import at.irian.ankor.ref.CollectionRef;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.impl.RefImplementor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Manfred Geiler
 */
@SuppressWarnings("UnusedDeclaration")
public class ListDiff<E> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ListDiff.class);

    private static final int DEFAULT_THRESHOLD = Integer.MAX_VALUE;

    private final CollectionRef listRef;
    private final List<E> oldList;
    private final List<E> newList;
    private final int threshold;

    public ListDiff(Ref listRef, List<E> oldList, List<E> newList) {
        this(listRef, oldList, newList, DEFAULT_THRESHOLD);
    }

    protected ListDiff(Ref listRef, List<E> oldList, List<E> newList, int threshold) {
        this.listRef = listRef.toCollectionRef();
        this.oldList = oldList;
        this.newList = newList;
        this.threshold = threshold;
    }

    public ListDiff<E> withThreshold(int threshold) {
        return new ListDiff<E>(listRef, oldList, newList, threshold);
    }


    public void applyChanges() {
        List<DiffChange<E>> diffChanges = calcDiffChanges();

        if (diffChanges.size() >= threshold) {
            listRef.setValue(newList);
            return;
        }

        for (DiffChange diffChange : diffChanges) {
            int index = diffChange.getIndex();
            switch (diffChange.getType()) {
                case delete:
                    listRef.delete(index);
                    break;
                case insert:
                    listRef.insert(index, diffChange.getElement());
                    break;
                case replace:
                    listRef.appendIndex(index).setValue(diffChange.getElement());
                    break;
                case none:
                default:
                    throw new IllegalStateException();
            }
        }
    }


    public void signalChanges() {

        List<DiffChange<E>> diffChanges = calcDiffChanges();

        if (diffChanges.size() >= threshold) {
            listRef.signalValueChange();
            return;
        }

        for (DiffChange diffChange : diffChanges) {
            int index = diffChange.getIndex();
            switch (diffChange.getType()) {
                case delete:
                    ((RefImplementor)listRef).signal(Change.deleteChange(index));
                    break;
                case insert:
                    ((RefImplementor)listRef).signal(Change.insertChange(index, diffChange.getElement()));
                    break;
                case replace:
                    ((RefImplementor)listRef.appendIndex(index)).signal(Change.valueChange(diffChange.getElement()));
                    break;
                case none:
                default:
                    throw new IllegalStateException();
            }
        }
    }


    protected List<DiffChange<E>> calcDiffChanges() {

        List<DiffChange<E>> result = new ArrayList<DiffChange<E>>();

        List<E> list1 = oldList;
        List<E> list2 = newList;

        int i = 0;
        while (!list1.isEmpty() || !list2.isEmpty()) {

            DiffChangeType diffChangeType = calcFirstElementChange(list1, list2);
            switch (diffChangeType) {
                case delete:
                    result.add(DiffChange.<E>delete(i));
                    list1 = restOf(list1);
                    break;
                case insert:
                    result.add(DiffChange.insert(i++, list2.get(0)));
                    list2 = restOf(list2);
                    break;
                case replace:
                    result.add(DiffChange.replace(i++, list2.get(0)));
                    list1 = restOf(list1);
                    list2 = restOf(list2);
                    break;
                case none:
                    i++;
                    list1 = restOf(list1);
                    list2 = restOf(list2);
                    break;
            }

            if (result.size() >= threshold) {
                return result;
            }

        }

        return result;
    }



    protected static <E> List<E> restOf(List<E> list) {
        if (list.isEmpty()) {
            return list;
        } else {
            return list.subList(1, list.size());
        }
    }


    protected DiffChangeType calcFirstElementChange(List<E> list1, List<E> list2) {
        if (list1.isEmpty()) {
            if (list2.isEmpty()) {
                // both lists are empty --> no change necessary
                return DiffChangeType.none;
            } else {
                // list 1 is empty, list 2 is not --> insert first element of list 2
                return DiffChangeType.insert;
            }
        }

        if (list2.isEmpty()) {
            // list 2 is empty, list 1 is not --> delete first element of list 1
            return DiffChangeType.delete;
        }

        // both lists are non-empty...

        E e1 = list1.get(0);
        int i2 = list2.indexOf(e1);

        if (i2 == 0) {
            // first element of list 1 is the same as first element in list 2 --> no change necessary
            return DiffChangeType.none;
        }

        E e2 = list2.get(0);
        int i1 = list1.indexOf(e2);

        if (i1 == 0) {
            // cannot happen (because this case was already caught by i2 == 0 above)
            // however ... may happen if element equals method has a bug and is non-symmetric
            return DiffChangeType.none;
        }

        if (i1 == -1 && i2 == -1) {
            // both first elements are not in the other list --> replace
            return DiffChangeType.replace;
        }

        if (i2 == -1) {
            // first element of list 1 is no longer in list 2 --> delete first element of list 1
            return DiffChangeType.delete;
        }

        if (i1 == -1) {
            // first element of list 2 was not found in list 1 --> insert first element of list 2
            return DiffChangeType.insert;
        }

        return DiffChangeType.replace;
    }


}
