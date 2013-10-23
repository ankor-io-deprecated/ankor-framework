package at.irian.ankor.viewmodel.watch;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author Manfred Geiler
 */
public abstract class AbstractExtendedList<E> extends AbstractList<E> implements ExtendedList<E> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AbstractExtendedList.class);

    @Override
    public boolean addAll(E... elements) {
        boolean added = false;
        for (E element : elements) {
            added |= add(element);
        }
        return added;
    }

    @Override
    public boolean setAll(E... elements) {
        return setAll(Arrays.asList(elements));
    }

    @Override
    public boolean setAll(Collection<? extends E> col) {
        clear();
        boolean added = false;
        for (E element : col) {
            added |= add(element);
        }
        return added;
    }

    @Override
    public boolean removeAll(E... elements) {
        boolean removed = false;
        for (E element : elements) {
            removed |= remove(element);
        }
        return removed;
    }

    @Override
    public boolean retainAll(E... elements) {
        return retainAll(Arrays.asList(elements));
    }

    @Override
    public void remove(int from, int to) {
        for (int i = from; i < to; i++) {
            remove(from);
        }
    }

}
