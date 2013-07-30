package at.irian.ankor.ref.listener;

import at.irian.ankor.change.ChangeEvent;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.ref.Ref;

/**
* @author Manfred Geiler
*/
class TreeRefChangeEventListener extends ChangeEventListener implements RefEventListenerImplementor {

    private final RefChangeListener listener;

    TreeRefChangeEventListener(Ref ref, RefChangeListener listener) {
        super(ref);
        this.listener = listener;
    }

    @Override
    public void process(ChangeEvent event) {
        Ref changedProperty = event.getChangedProperty();
        Ref watchedProperty = getWatchedProperty();
        if (isRelevantTreeChange(changedProperty, watchedProperty)) {
            listener.processChange(changedProperty);
        }
    }

    private boolean isRelevantTreeChange(Ref changedProperty, Ref watchedProperty) {
        return watchedProperty.equals(changedProperty) || watchedProperty.isAncestorOf(changedProperty);
    }

    @Override
    public RefListener getRefListener() {
        return listener;
    }

}
