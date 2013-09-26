package at.irian.ankor.viewmodel;

import at.irian.ankor.base.Wrapper;
import at.irian.ankor.ref.Ref;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Manfred Geiler
 */
@SuppressWarnings("UnusedDeclaration")
public class ViewModelListProperty<T extends List> implements Wrapper<T> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ViewModelListProperty.class);

    private Ref ref;
    private T value;
    private int threshold;

    ViewModelListProperty() {
        this(null, null);
    }

    public ViewModelListProperty(Ref parentObjectRef, String propertyName) {
        this(parentObjectRef, propertyName, null);
    }

    /**
     * Used on {@link #set(java.util.List)} of a new List value. If the percentage rows changed > current list size then
     * the list is changed. Else only the changed rows are set, therefore less change events get dispatched.
     * @param threshold in percent
     */
    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public ViewModelListProperty(Ref parentObjectRef, String propertyName, T initialValue) {
        this.ref = parentObjectRef != null ? parentObjectRef.appendPath(propertyName) : null;
        this.value = initialValue;
        this.threshold = 10;
    }

    public static <T extends List> ViewModelListProperty<T> createUnreferencedProperty() {
        return new ViewModelListProperty<T>(null, null, null);
    }

    public static <T extends List> ViewModelListProperty<T> createUnreferencedProperty(T initialValue) {
        return new ViewModelListProperty<T>(null, null, initialValue);
    }

    public static <T extends List> ViewModelListProperty<T> createReferencedProperty(Ref parentObjectRef, String propertyName) {
        return new ViewModelListProperty<T>(parentObjectRef, propertyName, null);
    }

    public static <T extends List> ViewModelListProperty<T> createReferencedProperty(Ref parentObjectRef, String propertyName, T initialValue) {
        return new ViewModelListProperty<T>(parentObjectRef, propertyName, initialValue);
    }


    public void set(T newValue) {


        if (this.ref == null) {
            //throw new IllegalStateException("no ref");
            LOG.warn("setting non-referencing object " + this, new IllegalStateException());
            putWrappedValue(newValue);
        } else if (this.ref.isValid()) {
            this.ref.setValue(newValue);
        } else {
            putWrappedValue(newValue);
        }
    }

    public void init(T newValue) {
        putWrappedValue(newValue);
    }

    public T get() {
        return getWrappedValue();
    }

    public Ref getRef() {
        return ref;
    }

    void setRef(Ref ref) {
        this.ref = ref;
    }

    @Override
    public final T getWrappedValue() {
        return this.value;
    }

    @Override
    public final void putWrappedValue(T newValue) {
        if (this.value == null) {
            this.value = newValue;
        } else {
            RowDiff diff = new RowDiff(value, newValue, threshold);
            if (diff.isHasResult()) {
                for (Integer index : diff.getChangedRows()) {
                    //noinspection unchecked
                    value.set(index, newValue.get(index));
                }
            } else {
                this.value = newValue;
            }
        }
    }

    public static class RowDiff {

        private List<Integer> changedRows;

        public RowDiff(List<Object> oldValue, List<Object> newValue, int threshold) {
            calculate(oldValue, newValue, threshold);
        }

        private void calculate(List<Object> oldValue, List<Object> newValue, int threshold) {
            if (oldValue != null && newValue != null) {
                int maxChanges = oldValue.size() * threshold / 100 + 1;
                if (oldValue.size() == newValue.size()) {
                    changedRows = new ArrayList<Integer>(maxChanges);
                    for (int i = 0; i < oldValue.size(); i++) {
                        if (!oldValue.get(i).equals(newValue.get(i))) {
                            if (changedRows.size() >= maxChanges - 1) {
                                break;
                            }
                            changedRows.add(i);
                        }
                    }
                }
            }
        }

        public boolean isHasResult() {
            return changedRows != null;
        }

        public List<Integer> getChangedRows() {
            if (changedRows == null) {
                return Collections.emptyList();
            } else {
                return changedRows;
            }
        }
    }
}
