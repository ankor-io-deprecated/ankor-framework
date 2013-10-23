package at.irian.ankor.viewmodel.diff;

import at.irian.ankor.change.Change;
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

    private final List<E> oldList;
    private final List<E> newList;
    private final int threshold;

    public ListDiff(List<E> oldList, List<E> newList) {
        this(oldList, newList, DEFAULT_THRESHOLD);
    }

    protected ListDiff(List<E> oldList, List<E> newList, int threshold) {
        this.oldList = oldList;
        this.newList = newList;
        this.threshold = threshold;
    }

    public ListDiff<E> withThreshold(int threshold) {
        return new ListDiff<E>(oldList, newList, threshold);
    }


    public void applyChangesTo(List<E> destinationList) {
        List<DiffChange<E>> diffChanges = calcDiffChanges();

        if (diffChanges == null) {
            destinationList.clear();
            destinationList.addAll(newList);
            return;
        }

        for (DiffChange<E> diffChange : diffChanges) {
            int index = diffChange.getIndex();
            switch (diffChange.getType()) {
                case deleteElement:
                    destinationList.remove(index);
                    break;
                case insertElement:
                    destinationList.add(index, diffChange.getElement());
                    break;
                case replaceElement:
                    destinationList.set(index, diffChange.getElement());
                    break;
                case none:
                default:
                    throw new IllegalStateException();
            }
        }
    }

    public void applyChangesTo(Ref listRef) {
        List<DiffChange<E>> diffChanges = calcDiffChanges();

        if (diffChanges == null) {
            listRef.setValue(newList);
            return;
        }

        for (int i = 0, len = diffChanges.size(); i < len; i++) {
            DiffChange<E> diffChange = diffChanges.get(i);
            int index = diffChange.getIndex();
            switch (diffChange.getType()) {
                case deleteElement:
                    listRef.toCollectionRef().delete(index);
                    break;
                case insertElement:
                    listRef.toCollectionRef().insert(index, diffChange.getElement());
                    break;
                case replaceElement:
                    // examine next elements to combine multiple replaces to one ref.replace call
                    int j;
                    for (j = 1; i + j < len; j++) {
                        DiffChange<E> nextDiffChange = diffChanges.get(i + j);
                        if (nextDiffChange.getType() != DiffChangeType.replaceElement
                            || nextDiffChange.getIndex() != index + j) {
                            break;
                        }
                    }
                    if (j > 1) {
                        List<E> replaceElements = new ArrayList<E>(j);
                        for (int k = 0; k < j; k++) {
                            replaceElements.add(diffChanges.get(i + k).getElement());
                        }
                        listRef.toCollectionRef().replace(index, replaceElements);
                        i = i + j - 1;
                    } else {
                        listRef.appendIndex(index).setValue(diffChange.getElement());
                    }
                    break;
                case none:
                default:
                    throw new IllegalStateException();
            }
        }
    }

    public void signalChangesFor(Ref listRef) {

        List<DiffChange<E>> diffChanges = calcDiffChanges();

        if (diffChanges == null) {
            listRef.signalValueChange();
            return;
        }

        signalChanges(listRef, diffChanges);
    }


    public static <E> void signalChanges(Ref listRef, List<DiffChange<E>> diffChanges) {
        for (int i = 0, len = diffChanges.size(); i < len; i++) {
            DiffChange<E> diffChange = diffChanges.get(i);
            int index = diffChange.getIndex();
            switch (diffChange.getType()) {
                case deleteElement:
                    ((RefImplementor)listRef).signal(Change.deleteChange(index));
                    break;
                case insertElement:
                    ((RefImplementor)listRef).signal(Change.insertChange(index, diffChange.getElement()));
                    break;
                case replaceElement:
                    // examine next elements to combine multiple replaces to one ref.replace call
                    int j;
                    for (j = 1; i + j < len; j++) {
                        DiffChange<E> nextDiffChange = diffChanges.get(i + j);
                        if (nextDiffChange.getType() != DiffChangeType.replaceElement
                            || nextDiffChange.getIndex() != index + j) {
                            break;
                        }
                    }
                    if (j > 1) {
                        List<E> replaceElements = new ArrayList<E>(j);
                        for (int k = 0; k < j; k++) {
                            replaceElements.add(diffChanges.get(i + k).getElement());
                        }
                        ((RefImplementor)listRef).signal(Change.replaceChange(index, replaceElements));
                        i = i + j - 1;
                    } else {
                        ((RefImplementor)listRef.appendIndex(index)).signal(Change.valueChange(diffChange.getElement()));
                    }
                    break;
                case none:
                default:
                    throw new IllegalStateException();
            }
        }
    }

    public List<DiffChange<E>> calcAllDiffChanges() {
        return internalCalcDiffChanges(Integer.MAX_VALUE);
    }

    /**
     * @return null if there are more diffs than this ListDiffs threshold
     */
    public List<DiffChange<E>> calcDiffChanges() {
        return internalCalcDiffChanges(threshold);
    }

    /**
     * @param threshold  max number of diffs
     * @return null if there are more diffs than the given threshold
     */
    private List<DiffChange<E>> internalCalcDiffChanges(int threshold) {
        List<DiffChange<E>> result = new ArrayList<DiffChange<E>>();

        List<E> list1 = oldList;
        List<E> list2 = newList;

        int i = 0;
        while (!list1.isEmpty() || !list2.isEmpty()) {

            DiffChangeType diffChangeType = calcFirstElementChange(list1, list2);
            switch (diffChangeType) {
                case deleteElement:
                    result.add(DiffChange.<E>delete(i));
                    list1 = restOf(list1);
                    break;
                case insertElement:
                    result.add(DiffChange.insert(i++, list2.get(0)));
                    list2 = restOf(list2);
                    break;
                case replaceElement:
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
                // shortcut return for situations where diff changes wont be used anyway
                return null;
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
                return DiffChangeType.insertElement;
            }
        }

        if (list2.isEmpty()) {
            // list 2 is empty, list 1 is not --> delete first element of list 1
            return DiffChangeType.deleteElement;
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
            return DiffChangeType.replaceElement;
        }

        if (i2 == -1) {
            // first element of list 1 is no longer in list 2 --> delete first element of list 1
            return DiffChangeType.deleteElement;
        }

        if (i1 == -1) {
            // first element of list 2 was not found in list 1 --> insert first element of list 2
            return DiffChangeType.insertElement;
        }

        return DiffChangeType.replaceElement;
    }


}
