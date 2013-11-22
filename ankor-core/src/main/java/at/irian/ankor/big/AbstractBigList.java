package at.irian.ankor.big;

import at.irian.ankor.messaging.AnkorIgnore;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.*;

/**
 * @author Manfred Geiler
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class AbstractBigList<E> extends AbstractList<E> implements BigList<E> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Lazy.class);

    @AnkorIgnore
    private NavigableMap<Integer, Reference> elements;

    private int size;

    protected AbstractBigList(int size) {
        this.elements = new TreeMap<Integer, Reference>();
        this.size = size;
    }

    protected AbstractBigList(Collection<? extends E> c) {
        this(c.size());
        int idx = 0;
        for (E e : c) {
            elements.put(idx++, createReferenceFor(e));
        }
    }

    @SuppressWarnings("unchecked")
    protected Reference createReferenceFor(E element) {
        return element != null ? new SoftReference(element) : new SoftReference(new NullDummy());
        //return element != null ? new WeakReference(element) : new WeakReference(new NullDummy());
    }

    @Override
    public int size() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public E get(int index) {
        Reference elementReference = elements.get(index);
        if (elementReference == null) {
            return getMissingElement(index);
        }
        Object item = elementReference.get();
        if (item instanceof NullDummy) {
            return null;
        } else if (item == null) {
            return getMissingElement(index);
        } else {
            //noinspection unchecked
            return (E)item;
        }
    }

    protected abstract E getMissingElement(int index);


    @Override
    public E set(int index, E element) {
        Reference prev = elements.put(index, createReferenceFor(element));
        adjustSize();
        return getReferencedValue(prev);
    }

    private E getReferencedValue(Reference referenced) {
        if (referenced != null) {
            Object prevItem = referenced.get();
            if (prevItem instanceof NullDummy) {
                return null;
            } else {
                //noinspection unchecked
                return (E)prevItem;
            }
        } else {
            return null;
        }
    }

    @Override
    public void add(int index, E element) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
        NavigableMap<Integer, Reference> tailMap = elements.tailMap(index, true);
        List<Map.Entry<Integer, Reference>> copyOfDescendingTailMapEntries
                = new ArrayList<Map.Entry<Integer, Reference>>(tailMap.descendingMap().entrySet());
        tailMap.clear();
        elements.put(index, createReferenceFor(element));
        for (Map.Entry<Integer, Reference> entry : copyOfDescendingTailMapEntries) {
            elements.put(entry.getKey() + 1, entry.getValue());
        }
        this.size++;
        adjustSize();
    }

    @Override
    public E remove(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
        NavigableMap<Integer, Reference> tailMap = elements.tailMap(index, false);
        List<Map.Entry<Integer, Reference>> copyOfTailMapEntries
                = new ArrayList<Map.Entry<Integer, Reference>>(tailMap.entrySet());
        Reference removed = elements.remove(index);
        for (Map.Entry<Integer, Reference> entry : copyOfTailMapEntries) {
            Integer idx = entry.getKey();
            Reference value = entry.getValue(); // !!! never inline this entry.getValue() call because entries seem to get reused in elements map causing nasty side effects...
            elements.remove(idx);
            elements.put(idx - 1, value);
        }
        this.size--;
        adjustSize();
        return getReferencedValue(removed);
    }

    private void adjustSize() {
        int realSize = elements.isEmpty() ? 0 : elements.lastEntry().getKey() + 1;
        this.size = Math.max(this.size, realSize);
    }


    @Override
    public void clear() {
        this.elements.clear();
        this.size = 0;
    }

    @Override
    public void reset() {
        this.elements.clear();
    }

    @Override
    public void cleanup() {
        for (Map.Entry<Integer, Reference> entry : elements.entrySet()) {
            if (entry.getValue().get() == null) {
                entry.setValue(null);
            }
        }
    }

    @Override
    public boolean isAvailable(int index) {
        Reference elementReference = elements.get(index);
        return elementReference != null && elementReference.get() != null;
    }

    private static class NullDummy {}

}
