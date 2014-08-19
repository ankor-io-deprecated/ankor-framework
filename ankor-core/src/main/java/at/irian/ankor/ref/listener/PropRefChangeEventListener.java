package at.irian.ankor.ref.listener;

import at.irian.ankor.change.ChangeEvent;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.ref.Ref;

/**
* @author Manfred Geiler
*/
class PropRefChangeEventListener extends ChangeEventListener implements RefEventListenerImplementor {

    private final RefChangeListener listener;

    PropRefChangeEventListener(Ref ref, RefChangeListener listener) {
        super(ref);
        this.listener = listener;
    }

    @Override
    public void process(ChangeEvent event) {
        Ref changedProperty = event.getProperty();
        Ref watchedProperty = getWatchedProperty();
        if (isRelevantPropChange(changedProperty, watchedProperty)) {
            listener.processChange(changedProperty);
        }
    }

    private boolean isRelevantPropChange(Ref changedProperty, Ref watchedProperty) {
        return watchedProperty.equals(changedProperty) || watchedProperty.isDescendantOf(changedProperty);
    }

    @Override
    public RefListener getRefListener() {
        return listener;
    }

}
