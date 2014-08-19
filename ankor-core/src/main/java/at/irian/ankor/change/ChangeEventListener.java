package at.irian.ankor.change;

import at.irian.ankor.event.PropertyWatcherEventListener;
import at.irian.ankor.ref.Ref;

/**
* @author Manfred Geiler
*/
public abstract class ChangeEventListener extends PropertyWatcherEventListener {

    public ChangeEventListener(Ref watchedProperty) {
        super(watchedProperty);
    }

    public abstract void process(ChangeEvent event);
}
