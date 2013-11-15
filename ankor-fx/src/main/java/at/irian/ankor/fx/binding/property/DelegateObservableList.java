package at.irian.ankor.fx.binding.property;

import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @deprecated  use {@link at.irian.ankor.fx.binding.value.ObservableRef} or {@link RefProperty}
 * @see at.irian.ankor.fx.binding.fxref.FxRefs
 */
@Deprecated
public class DelegateObservableList<T> extends AbstractList<T> implements ObservableList<T> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DelegateObservableList.class);

    private final List<T> list;
    private final List<InvalidationListener> invalidationListeners = new ArrayList<>();
    private final List<ListChangeListener<T>> listChangeListeners = new ArrayList<>();

    public DelegateObservableList(List<T> list) {
        this.list = list;
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        invalidationListeners.add(invalidationListener);
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {
        invalidationListeners.remove(invalidationListener);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addListener(ListChangeListener listChangeListener) {
        listChangeListeners.add(listChangeListener);
    }

    @Override
    public void removeListener(ListChangeListener listChangeListener) {
        listChangeListeners.remove(listChangeListener);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get(int i) {
        return list.get(i);
    }

    @Override
    public int size() {
        return list.size();
    }

    @SuppressWarnings("UnusedDeclaration")
    public void callObservers() {
        for (InvalidationListener listener : invalidationListeners) {
            listener.invalidated(this);
        }
    }

    public void callObservers(ListChangeListener.Change<T> change) {
        for (ListChangeListener<T> listener : new ArrayList<>(listChangeListeners)) {
            listener.onChanged(change);
        }
    }

    @SafeVarargs
    @Override
    public final boolean addAll(T... ts) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(int i, int i2) {
        throw new UnsupportedOperationException();
    }

    @SafeVarargs
    @Override
    public final boolean removeAll(T... ts) {
        throw new UnsupportedOperationException();
    }

    @SafeVarargs
    @Override
    public final boolean retainAll(T... ts) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setAll(Collection<? extends T> ts) {
        throw new UnsupportedOperationException();
    }

    @SafeVarargs
    @Override
    public final boolean setAll(T... ts) {
        throw new UnsupportedOperationException();
    }
}
