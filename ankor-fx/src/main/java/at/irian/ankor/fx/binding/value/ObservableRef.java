package at.irian.ankor.fx.binding.value;

import at.irian.ankor.change.Change;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.change.ChangeType;
import at.irian.ankor.fx.binding.cache.FxCacheSupport;
import at.irian.ankor.ref.Ref;
import com.sun.javafx.binding.ExpressionHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.util.Callback;

/**
 * A JavaFX ObservableValue backed by a Ankor Ref.
 * The value of this observable is directly retrieved from the underlying Ref.
 * Listeners of this observable get notified when the value of the underlying Ref changes.
 *
 * @author Manfred Geiler
 */
public class ObservableRef<T> implements ObservableValue<T> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ObservableRef.class);

    protected final Ref ref;
    private final T defaultValue;
    private final ChangeEventListener changeEventListener;

    private ExpressionHelper<T> helper = null;

    protected ObservableRef(Ref ref, T defaultValue) {
        this.ref = ref;
        this.defaultValue = defaultValue;
        this.changeEventListener = new ObservableChangeEventListener(ref, this) {
            @Override
            protected void handleChange(Ref changedProperty, Change change) {
                if (change.getType() == ChangeType.value
                    && (changedProperty.equals(ObservableRef.this.ref) || changedProperty.isAncestorOf(ObservableRef.this.ref))) {
                    LOG.trace("{} changed value --> fire FX change event", changedProperty);
                    ExpressionHelper.fireValueChangedEvent(helper);
                }
            }
        };
        this.ref.context().modelContext().getEventListeners().add(this.changeEventListener);
    }


    public static <T> ObservableValue<T> createObservableValue(Ref ref) {
        return createObservableValue(ref, null);
    }

    public static <T>  ObservableValue<T> createObservableValue(Ref ref, final T defaultValue) {
        return FxCacheSupport.getBindingCache(ref)
                             .getObservableValue(ref, defaultValue, new Callback<Ref, ObservableValue<T>>() {
                                 @Override
                                 public ObservableValue<T> call(Ref ref) {
                                     LOG.debug("Creating ObservableValue for {}", ref);
                                     return new ObservableRef<>(ref, defaultValue);
                                 }
                             });
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
        try {
            return ref.getValue();
        } catch (IllegalStateException e) {
            return defaultValue;
        }
    }

    protected void finalize() throws Throwable {
        this.ref.context().modelContext().getEventListeners().remove(this.changeEventListener);
        super.finalize();
    }

}
