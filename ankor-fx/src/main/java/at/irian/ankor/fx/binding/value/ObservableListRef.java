package at.irian.ankor.fx.binding.value;

import at.irian.ankor.change.Change;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.fx.binding.cache.FxCacheSupport;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.util.RefList;
import com.sun.javafx.collections.ListListenerHelper;
import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Callback;

import java.util.Arrays;
import java.util.Collection;

/**
 * A JavaFX ObservableList backed by a Ankor Ref that references a List.
 * The list items of this observable list are directly retrieved from the underlying "collection" Ref.
 * Listeners of this observable get notified whenever the referenced list's content changes.
 *
 * @author Manfred Geiler
 */
public class ObservableListRef<E> extends RefList<E> implements ObservableList<E> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ObservableListRef.class);

    private final FxListChangeHelper<E> changeHelper;
    private final ChangeEventListener changeEventListener;

    private ListListenerHelper<E> listenerHelper = null;

    protected ObservableListRef(Ref ref) {
        super(ref);
        this.changeHelper = new FxListChangeHelper<>(this);
        this.changeEventListener = new ObservableChangeEventListener(ref, this) {
            @Override
            protected void handleChange(Ref changedProperty, Change change) {
                if (changedProperty.equals(ObservableListRef.this.listRef)) {
                    switch (change.getType()) {
                        case value:
                            // list itself was replaced --> ignore here, handled by the wrapping ObservableValue
                            break;
                        case insert:
                        case delete:
                        case replace:
                            ListListenerHelper.fireValueChangedEvent(listenerHelper, changeHelper.toFxChange(change));
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown change type " + change.getType());
                    }
                }
            }
        };
        this.listRef.context().modelSession().getEventListeners().add(this.changeEventListener);
    }


    public static <T> ObservableList<T> createObservableList(Ref ref) {
        return FxCacheSupport.getBindingCache(ref)
                             .getObservableList(ref, null, new Callback<Ref, ObservableList<T>>() {
                                 @Override
                                 public ObservableList<T> call(Ref ref) {
                                     LOG.debug("Creating ObservableList for {}", ref);
                                     return new ObservableListRef<>(ref);
                                 }
                             });
    }


    @Override
    public void addListener(InvalidationListener invalidationlistener) {
        listenerHelper = ListListenerHelper.addListener(listenerHelper, invalidationlistener);
    }

    @Override
    public void removeListener(InvalidationListener invalidationlistener) {
        listenerHelper = ListListenerHelper.removeListener(listenerHelper, invalidationlistener);
    }

    @Override
    public void addListener(ListChangeListener<? super E> listChangeListener) {
        listenerHelper = ListListenerHelper.addListener(listenerHelper, listChangeListener);
    }

    @Override
    public void removeListener(ListChangeListener<? super E> listChangeListener) {
        listenerHelper = ListListenerHelper.removeListener(listenerHelper, listChangeListener);
    }



    // ObservableList extensions

    @SuppressWarnings("unchecked")
    @Override
    public boolean addAll(E... es) {
        boolean added = false;
        for (E e : es) {
            added |= add(e);
        }
        return added;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean setAll(E... es) {
        return setAll(Arrays.asList(es));
    }

    @Override
    public boolean setAll(Collection<? extends E> ts) {
        // todo optimize
        clear();
        return addAll(ts);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean removeAll(E... es) {
        boolean removed = false;
        for (E e : es) {
            removed |= remove(e);
        }
        return removed;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean retainAll(E... es) {
        return retainAll(Arrays.asList(es));
    }

    @Override
    public void remove(int from, int to) {
        // todo  test this
        for (int i = from, len = size(); i < to && i < len; i++) {
            remove(from);
        }
    }



    // misc

    protected void finalize() throws Throwable {
        this.listRef.context().modelSession().getEventListeners().remove(this.changeEventListener);
        super.finalize();
    }

}
