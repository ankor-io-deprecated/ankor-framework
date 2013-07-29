package at.irian.ankor.dispatch;

import at.irian.ankor.action.Action;
import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public interface Dispatcher {

    void dispatchAction(Ref actionProperty, Action action);

    void dispatchChange(Ref changedProperty, Object newValue);

}
