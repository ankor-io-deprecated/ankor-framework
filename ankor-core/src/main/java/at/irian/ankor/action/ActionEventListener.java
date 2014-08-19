package at.irian.ankor.action;

import at.irian.ankor.event.PropertyWatcherEventListener;
import at.irian.ankor.ref.Ref;

/**
* @author Manfred Geiler
*/
public abstract class ActionEventListener extends PropertyWatcherEventListener {

    public ActionEventListener(Ref watchedProperty) {
        super(watchedProperty);
    }

    public abstract void process(ActionEvent event);
}
