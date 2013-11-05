package at.irian.ankor.change;

import at.irian.ankor.base.ObjectUtils;
import at.irian.ankor.event.ModelEventListener;
import at.irian.ankor.event.source.Source;
import at.irian.ankor.ref.Ref;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class OldValuesAwareChangeEvent extends ChangeEvent {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(OldValuesAwareChangeEvent.class);

    private final Object oldChangedValue;
    private final Map<Ref, Object> oldWatchedValues;

    public OldValuesAwareChangeEvent(Source source,
                                     Ref changedProperty,
                                     Change change,
                                     Object oldChangedValue,
                                     Map<Ref, Object> oldWatchedValues) {
        super(source, changedProperty, change);
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

        //todo, this does not work unless we are using deep copies for the old values

        Change change = getChange();
        if (watchedProperty == null) {
            // this is a global listener
            // invoke listener if the CHANGED property has actually changed it's value
            return isModification(change) || isDifferent(getOldChangedValue(), change.getValue());
        }

        Ref changedProperty = getChangedProperty();
        if (watchedProperty.equals(changedProperty)) {
            // listener watches exactly this ref
            // invoke listener if the CHANGED/WATCHED property has actually changed it's value
            return isModification(change) || isDifferent(getOldChangedValue(), change.getValue());
        }

        if (changedProperty.isDescendantOf(watchedProperty)) {
            // changed property is a descendant of the watched property
            // invoke listener only if the CHANGED property has actually changed it's value
            return isModification(change) || isDifferent(getOldChangedValue(), change.getValue());
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

    private boolean isModification(Change change) {
        ChangeType changeType = change.getType();
        return changeType == ChangeType.insert
               || changeType == ChangeType.delete
               || changeType == ChangeType.replace;
    }

    private boolean isDifferent(Object changedPropOldValue, Object changedPropNewValue) {
        return !ObjectUtils.nullSafeEquals(changedPropOldValue, changedPropNewValue);
    }

}
