package at.irian.ankor.viewmodel;

import at.irian.ankor.ref.Ref;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

public class ViewModelListBase<T> extends ViewModelBase {

    protected List<T> list;

    public ViewModelListBase(Ref viewModelRef, String name, List<T> list) {
        super(viewModelRef.append(name));
        this.list = new ArrayList<T>(list);
    }

    private Ref listRef() {
        return thisRef().append("list");
    }

    private Ref listRef(int i) {
        return thisRef().append(String.format("list[%s]", i));
}

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public boolean contains(Object o) {
        return list.contains(o);
    }

    public Iterator<T> iterator() {
        return list.iterator();
    }

    public Object[] toArray() {
        return list.toArray();
    }

    public <T1> T1[] toArray(T1[] a) {
        return list.toArray(a);
    }

    public boolean add(T t) {
        list.add((T) new Object());
        listRef(size() - 1).setValue(t);
        return true;
    }

    public boolean remove(Object o) {
        int index = indexOf(o);
        boolean containsElement = (index >= 0);
        if (containsElement) {
            remove(index);
        }
        return containsElement;
    }

    public boolean containsAll(Collection<?> c) {
        throw new NotImplementedException();
    }

    public boolean addAll(Collection<? extends T> c) {
        throw new NotImplementedException();
    }

    public boolean addAll(int index, Collection<? extends T> c) {
        throw new NotImplementedException();
    }

    public boolean removeAll(Collection<?> c) {
        throw new NotImplementedException();
    }

    public boolean retainAll(Collection<?> c) {
        throw new NotImplementedException();
    }

    public void clear() {
        listRef().setValue(new ArrayList<T>());
    }

    public T get(int index) {
        return list.get(index);
    }

    public T set(int index, T element) {
        T oldValue = get(index);
        listRef(index).setValue(element);
        return oldValue;
    }

    /**
     * Starting from index, shifts all elements of the list to the left (closer to 0),
     * while informing the client about the changes.
     *
     * The last element of the list will be set to null, while the element at index will be be removed from the list.
     * Note that this will not shift the entire list like a typical shift functions.
     *
     * @param index The index from which to start the shift.
     *              The element on this position will be removed from the collection.
     */
    private void shiftLeft(int index) {
        int i;
        for(i = index; i < size() - 1; i++) {
            listRef(i).setValue(get(i + 1));
        }

        listRef(i).setValue(null);
        list.remove(list.size()-1);
    }

    /**
     * Starting from index + 1, shifts all elements of the list to the right (away from 0),
     * while informing the client about the changes.
     *
     * The element at index and the element at index + 1 will be identical after the shift.
     * Note that this will not shift the entire List like typical shift functions.
     *
     * @param index The index from which to start the shift.
     *              The element on this position and the next will be identical after the shift.
     */
    private void shiftRight(int index) {
        list.add((T) new Object());

        int i;
        for(i = size() - 1; i > index; i--) {
            listRef(i).setValue(this.get(i - 1));
        }
    }


    public void add(int index, T element) {
        shiftRight(index);
        listRef(index).setValue(element);
    }

    public T remove(int index) {
        T toRemove = get(index);
        shiftLeft(index);
        return toRemove;
    }

    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    public ListIterator<T> listIterator() {
        return list.listIterator();
    }

    public ListIterator<T> listIterator(int index) {
        return list.listIterator(index);
    }

    public List<T> subList(int fromIndex, int toIndex) {
        throw new NotImplementedException();
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
