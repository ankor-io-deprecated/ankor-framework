package at.irian.ankor.change;

import at.irian.ankor.event.PropertyWatcherModelEventListener;
import at.irian.ankor.ref.Ref;

/**
* @author Manfred Geiler
*/
public abstract class ChangeEventListener extends PropertyWatcherModelEventListener {

    public ChangeEventListener(Ref watchedProperty) {
        super(watchedProperty);
    }

    public abstract void process(ChangeEvent event);
}
