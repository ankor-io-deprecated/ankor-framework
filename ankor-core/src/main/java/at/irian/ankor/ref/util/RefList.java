package at.irian.ankor.ref.util;

import at.irian.ankor.change.Change;
import at.irian.ankor.event.source.ModelSource;
import at.irian.ankor.ref.CollectionRef;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.impl.RefImplementor;

import java.util.*;

/**
 * A List backed by a Ankor Ref that references a List.
 *
 * @author Manfred Geiler
 */
public class RefList<E> extends AbstractList<E> implements List<E> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RefList.class);

    protected final CollectionRef listRef;

    public RefList(Ref ref) {
        this.listRef = ref.toCollectionRef();
    }

    private List<E> getReferencedList() {
        List<E> list = null;
        try {
            list = listRef.getValue();
        } catch (Exception e) {
            LOG.debug("Cannot resolve valua of {}", listRef);
        }
        return list != null ? list : Collections.<E>emptyList();
    }


    // read methods

    @Override
    public E get(int i) {
        return getReferencedList().get(i);
    }

    @Override
    public int size() {
        return getReferencedList().size();
    }

    @Override
    public boolean isEmpty() {
        return getReferencedList().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return getReferencedList().contains(o);
    }

    @Override
    public Object[] toArray() {
        return getReferencedList().toArray();
    }

    @SuppressWarnings("SuspiciousToArrayCall")
    @Override
    public <T> T[] toArray(T[] a) {
        return getReferencedList().toArray(a);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return getReferencedList().containsAll(c);
    }

    @Override
    public int indexOf(Object o) {
        return getReferencedList().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return getReferencedList().lastIndexOf(o);
    }



    // write methods

    @Override
    public void add(int index, E element) {
        ((RefImplementor)listRef).apply(new ModelSource(listRef, this), Change.insertChange(index, element));
    }

    @Override
    public E remove(int index) {
        E oldValue = get(index);
        ((RefImplementor)listRef).apply(new ModelSource(listRef, this), Change.deleteChange(index));
        return oldValue;
    }

    @Override
    public E set(int index, E element) {
        E oldVal = listRef.appendIndex(index).getValue();
        ((RefImplementor)listRef).apply(new ModelSource(listRef, this),
                                        Change.replaceChange(index, Collections.singleton(element)));
        return oldVal;
    }


    // write methods (that maybe optimized later as soon as Ankor supports more complex changes)

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return super.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return super.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return super.retainAll(c);
    }

    @Override
    public void clear() {
        super.clear();
    }


    // potential write methods

    @Override
    public Iterator<E> iterator() {
        return super.iterator();
    }

    @Override
    public ListIterator<E> listIterator() {
        return super.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return super.listIterator(0);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return super.subList(fromIndex, toIndex);
    }

}
