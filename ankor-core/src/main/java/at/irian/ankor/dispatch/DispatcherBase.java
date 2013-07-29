package at.irian.ankor.dispatch;

import at.irian.ankor.action.Action;
import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public abstract class DispatcherBase implements Dispatcher {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DispatcherBase.class);


    @Override
    public void dispatchAction(Ref actionProperty, Action action) {
        actionProperty.fireAction(action);
    }

    @Override
    public void dispatchChange(Ref changedProperty, Object newValue) {
        changedProperty.setValue(newValue);
    }
}
