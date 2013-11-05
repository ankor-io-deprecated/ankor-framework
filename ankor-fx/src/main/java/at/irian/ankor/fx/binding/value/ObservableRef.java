package at.irian.ankor.fx.binding.value;

import at.irian.ankor.change.ChangeEvent;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.event.source.CustomSource;
import at.irian.ankor.ref.Ref;
import com.sun.javafx.binding.ExpressionHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * @author Manfred Geiler
 */
public class ObservableRef<T> implements ObservableValue<T> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ObservableRef.class);

    protected final Ref ref;
    private final ChangeEventListener changeEventListener;

    private ExpressionHelper<T> helper = null;

    public ObservableRef(Ref ref) {
        this.ref = ref;
        this.changeEventListener = new ChangeEventListener(ref) {
            @Override
            public void process(ChangeEvent event) {
                if (event.getSource() instanceof CustomSource) {
                    if (((CustomSource) event.getSource()).getCustomSourceObject() == ObservableRef.this) {
                        // ignore this change event because it originates from this Observable itself
                        return;
                    }
                }
                Ref changedProperty = event.getChangedProperty();
                Ref watchedProperty = getWatchedProperty();
                if (watchedProperty.equals(changedProperty) || watchedProperty.isAncestorOf(changedProperty)) {
                    // the observed ref itself or a property in the ancestor tree has changed...
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
