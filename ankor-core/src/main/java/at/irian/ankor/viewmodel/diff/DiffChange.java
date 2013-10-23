package at.irian.ankor.viewmodel.diff;

/**
 * @author Manfred Geiler
 */
public class DiffChange<E> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DiffChange.class);

    private DiffChangeType type;
    private int index;
    private E element;

    private DiffChange(DiffChangeType type, int index, E element) {
        this.type = type;
        this.index = index;
        this.element = element;
    }

    public static <E> DiffChange<E> insert(int index, E element) {
        return new DiffChange<E>(DiffChangeType.insertElement, index, element);
    }

    public static <E> DiffChange<E> delete(int index) {
        return new DiffChange<E>(DiffChangeType.deleteElement, index, null);
    }

    public static <E> DiffChange<E> replace(int index, E element) {
        return new DiffChange<E>(DiffChangeType.replaceElement, index, element);
    }

    DiffChangeType getType() {
        return type;
    }

    int getIndex() {
        return index;
    }

    E getElement() {
        return element;
    }
}
