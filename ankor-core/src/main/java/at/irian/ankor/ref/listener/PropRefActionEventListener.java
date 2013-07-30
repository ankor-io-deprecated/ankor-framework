package at.irian.ankor.ref.listener;

import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.action.ActionEventListener;
import at.irian.ankor.ref.Ref;

/**
* @author Manfred Geiler
*/
class PropRefActionEventListener extends ActionEventListener implements RefEventListenerImplementor {

    private final RefActionListener listener;

    PropRefActionEventListener(Ref ref, RefActionListener listener) {
        super(ref);
        this.listener = listener;
    }

    @Override
    public void process(ActionEvent event) {
        Ref watchedProperty = getWatchedProperty();
        if (isRelevantActionProperty(event.getActionProperty(), watchedProperty)) {
            listener.processAction(watchedProperty, event.getAction());
        }
    }

    private boolean isRelevantActionProperty(Ref actionProperty, Ref watchedProperty) {
        return watchedProperty.equals(actionProperty);
    }

    @Override
    public RefListener getRefListener() {
        return listener;
    }
}
