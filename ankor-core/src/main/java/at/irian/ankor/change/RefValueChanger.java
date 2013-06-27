package at.irian.ankor.change;

import at.irian.ankor.event.ModelEventListener;
import at.irian.ankor.event.PropertyWatcher;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.impl.AbstractRef;
import at.irian.ankor.ref.impl.RefContextImplementor;
import at.irian.ankor.util.ObjectUtils;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class RefValueChanger {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RefValueChanger.class);

    private final Ref changedProperty;
    private final RefContextImplementor context;
    private final ChangeEvent changeEvent;

    public RefValueChanger(AbstractRef changedProperty) {
        this.changedProperty = changedProperty;
        this.context = changedProperty.context();
        this.changeEvent = new ChangeEvent(changedProperty);
    }

    public void setValueTo(Object refPropNewValue, SetValueCallback callback) {
        // determine eligible change event listeners and remember old watched values
        Map<ModelEventListener, Object> listeners = findAppropriateListeners();

        if (listeners.isEmpty()) {
            // there is no proper change event listener for this ref
            // just set the new value and return
            callback.doSetValue();
            return;
        }

        // remember old value of the referenced property
        Object refPropOldValue = changedProperty.getValue();

        // set the new value
        callback.doSetValue();

        // invoke all listeners of which the relevant property has actually changed
        for (Map.Entry<ModelEventListener, Object> entry : listeners.entrySet()) {
            ModelEventListener listener = entry.getKey();
            Object watchedPropOldValue = entry.getValue();

            Ref watchedProperty = null;
            if (listener instanceof PropertyWatcher) {
                watchedProperty = ((PropertyWatcher) listener).getWatchedProperty();
            }

            if (isInvokeListener(watchedProperty, watchedPropOldValue, refPropOldValue, refPropNewValue)) {
                invokeListener(listener);
            }
        }

    }

    private Map<ModelEventListener, Object> findAppropriateListeners() {
        IdentityHashMap<ModelEventListener, Object> result = new IdentityHashMap<ModelEventListener, Object>();
        for (ModelEventListener listener : context.allEventListeners()) {
            if (changeEvent.isAppropriateListener(listener)) {
                Object oldWatchedValue = null;
                if (listener instanceof PropertyWatcher) {
                    Ref watchedProperty = ((PropertyWatcher) listener).getWatchedProperty();
                    if (watchedProperty != null) {
                        oldWatchedValue = watchedProperty.getValue();
                    }
                }
                result.put(listener, oldWatchedValue);
            }
        }
        return result;
    }

    private boolean isInvokeListener(Ref watchedProperty,
                                     Object watchedPropOldValue,
                                     Object changedPropOldValue,
                                     Object changedPropNewValue) {
        if (watchedProperty == null) {
            // this is a global listener
            // invoke listener if the CHANGED property has actually changed it's value
            return isDifferent(changedPropOldValue, changedPropNewValue);
        }

        if (watchedProperty.equals(changedProperty)) {
            // listener watches exactly this ref
            // invoke listener if the CHANGED/WATCHED property has actually changed it's value
            return isDifferent(changedPropOldValue, changedPropNewValue);
        }

        if (changedProperty.isDescendantOf(watchedProperty)) {
            // changed property is a descendant of the watched property
            // invoke listener only if the CHANGED property has actually changed it's value
            return isDifferent(changedPropOldValue, changedPropNewValue);
        }

        if (changedProperty.isAncestorOf(watchedProperty)) {
            // changed property is an ancestor of the watched property
            // invoke listener only if the WATCHED property has actually changed it's value
            Object watchedPropNewValue;
            if (watchedProperty.isValid()) {
                watchedPropNewValue = watchedProperty.getValue();
            } else {
                watchedPropNewValue = null;
            }
            return isDifferent(watchedPropOldValue, watchedPropNewValue);
        }

        return false;
    }


    private boolean isDifferent(Object changedPropOldValue, Object changedPropNewValue) {
        return !ObjectUtils.nullSafeEquals(changedPropOldValue, changedPropNewValue);
    }

    private void invokeListener(ModelEventListener listener) {
        try {
            changeEvent.processBy(listener);
        } catch (Exception e) {
            LOG.error("Listener " + listener + " threw exception while processing " + changeEvent, e);
        }
    }


    public interface SetValueCallback {
        void doSetValue();
    }
}
