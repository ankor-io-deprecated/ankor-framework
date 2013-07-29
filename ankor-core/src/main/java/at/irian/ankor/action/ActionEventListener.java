package at.irian.ankor.action;

import at.irian.ankor.event.PropertyWatcherModelEventListener;
import at.irian.ankor.ref.Ref;

/**
* @author Manfred Geiler
*/
public abstract class ActionEventListener extends PropertyWatcherModelEventListener {

    public ActionEventListener(Ref watchedProperty) {
        super(watchedProperty);
    }

    public abstract void process(ActionEvent event);
}
