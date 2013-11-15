package at.irian.ankor.fx.binding.value;

import at.irian.ankor.change.Change;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.change.ChangeType;
import at.irian.ankor.ref.Ref;
import com.sun.javafx.binding.ExpressionHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * A JavaFX ObservableValue backed by a Ankor Ref.
 * The value of this observable is directly retrieved from the underlying Ref.
 * Listeners of this observable get notified when the value of the underlying Ref changes.
 *
 * @author Manfred Geiler
 */
public class ObservableRef<T> implements ObservableValue<T> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ObservableRef.class);

    protected final Ref ref;
    private final ChangeEventListener changeEventListener;

    private ExpressionHelper<T> helper = null;

    public ObservableRef(Ref ref) {
        this.ref = ref;
        this.changeEventListener = new ObservableChangeEventListener(ref, this) {
            @Override
            protected void handleChange(Ref changedProperty, Change change) {
                if (changedProperty.equals(ObservableRef.this.ref) && change.getType() == ChangeType.value) {
                    ExpressionHelper.fireValueChangedEvent(helper);
                }
            }
        };
        this.ref.context().modelContext().getEventListeners().add(this.changeEventListener);
    }

    @Override
    public void addListener(ChangeListener<? super T> changeListener) {
        helper = ExpressionHelper.addListener(helper, this, changeListener);
    }

    @Override
    public void removeListener(ChangeListener<? super T> changeListener) {
        helper = ExpressionHelper.removeListener(helper, changeListener);
    }

    @Override
    public void addListener(InvalidationListener invalidationlistener) {
        helper = ExpressionHelper.addListener(helper, this, invalidationlistener);
    }

    @Override
    public void removeListener(InvalidationListener invalidationlistener) {
        helper = ExpressionHelper.removeListener(helper, invalidationlistener);
    }

    @Override
    public T getValue() {
        return ref.getValue();
    }

    protected void finalize() throws Throwable {
        this.ref.context().modelContext().getEventListeners().remove(this.changeEventListener);
        super.finalize();
    }

}
