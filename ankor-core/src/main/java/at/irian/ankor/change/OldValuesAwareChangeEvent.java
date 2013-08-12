package at.irian.ankor.change;

import at.irian.ankor.base.ObjectUtils;
import at.irian.ankor.event.ModelEventListener;
import at.irian.ankor.ref.Ref;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class OldValuesAwareChangeEvent extends ChangeEvent {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(OldValuesAwareChangeEvent.class);

    private final Object oldChangedValue;
    private final Map<Ref, Object> oldWatchedValues;

    public OldValuesAwareChangeEvent(Ref changedProperty,
                                     Change change,
                                     Object oldChangedValue,
                                     Map<Ref, Object> oldWatchedValues) {
        super(changedProperty, change);
        this.oldChangedValue = oldChangedValue;
        this.oldWatchedValues = oldWatchedValues;
    }

    public Object getOldChangedValue() {
        return oldChangedValue;
    }

    public Object getOldWatchedValue(Ref watchedProperty) {
        return oldWatchedValues.get(watchedProperty);
    }

    @Override
    public boolean isAppropriateListener(ModelEventListener listener) {
        if (!(listener instanceof ChangeEventListener)) {
            return false;
        }

        ChangeEventListener changeEventListener = (ChangeEventListener) listener;
        Ref watchedProperty = changeEventListener.getWatchedProperty();

        Object changedPropOldValue = getOldChangedValue();
        Object changedPropNewValue = getChange().getNewValue();
        if (watchedProperty == null) {
            // this is a global listener
            // invoke listener if the CHANGED property has actually changed it's value
            return isDifferent(changedPropOldValue, changedPropNewValue);
        }

        Ref changedProperty = getChangedProperty();
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
                Object watchedPropOldValue = getOldWatchedValue(watchedProperty);
                watchedPropNewValue = watchedProperty.getValue();
                return isDifferent(watchedPropOldValue, watchedPropNewValue);
            } else {
                return false;
            }
        }

        return false;
    }


    private boolean isDifferent(Object changedPropOldValue, Object changedPropNewValue) {
        // XXX: For proof-of-concept
        //return !ObjectUtils.nullSafeEquals(changedPropOldValue, changedPropNewValue);
        return true;
    }

}
