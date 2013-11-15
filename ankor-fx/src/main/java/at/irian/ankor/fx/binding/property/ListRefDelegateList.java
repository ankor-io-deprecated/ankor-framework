package at.irian.ankor.fx.binding.property;

import at.irian.ankor.ref.CollectionRef;
import at.irian.ankor.ref.Ref;

import java.util.AbstractList;
import java.util.List;

/**
 * @deprecated  use {@link at.irian.ankor.fx.binding.value.ObservableRef} or {@link RefProperty}
 * @see at.irian.ankor.fx.binding.fxref.FxRefs
 */
@Deprecated
public class ListRefDelegateList<T> extends AbstractList<T> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ListRefDelegateList.class);

    private final CollectionRef listRef;

    public ListRefDelegateList(Ref listRef) {
        this.listRef = listRef.toCollectionRef();
    }

    @Override
    public T get(int i) {
        return listRef.appendIndex(i).getValue();
    }

    @Override
    public int size() {
        List list = listRef.getValue();
        return list != null ? list.size() : 0;
    }

    @Override
    public void add(int index, T element) {
        listRef.insert(index, element);
    }

    @Override
    public T remove(int index) {
        T oldValue = get(index);
        listRef.delete(index);
        return oldValue;
    }

    @Override
    public T set(int index, T element) {
        Ref entryRef = listRef.appendIndex(index);
        T oldVal = entryRef.getValue();
        entryRef.setValue(element);
        return oldVal;
    }
}
