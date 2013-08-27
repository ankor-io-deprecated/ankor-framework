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
        return thisRef("list");
    }

    private Ref listRef(int i) {
        return listRef().appendIdx(i);
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
        listRef().insert(size(), t);
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

    public void reset(List<? extends T> c) {
        listRef().setValue(c);
    }

    public T get(int index) {
        return list.get(index);
    }

    public T set(int index, T element) {
        T oldValue = get(index);
        listRef(index).setValue(element);
        return oldValue;
    }

    public void add(int index, T element) {
        listRef().insert(index, element);
    }

    public T remove(int index) {
        T toRemove = get(index);
        listRef(index).delete();
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
