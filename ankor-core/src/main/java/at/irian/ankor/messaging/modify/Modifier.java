package at.irian.ankor.messaging.modify;

import at.irian.ankor.action.Action;
import at.irian.ankor.change.Change;
import at.irian.ankor.ref.Ref;

/**
 * Pluggable feature that may be used to modify the content of a message before it is serialized and sent
 * and/or modify it after it was received and deserialized.
 *
 * @author Manfred Geiler
 */
public interface Modifier {

    Change modifyBeforeSend(Change change, Ref changedProperty);
    Change modifyAfterReceive(Change change, Ref changedProperty);

    Action modifyBeforeSend(Action action, Ref actionProperty);
    Action modifyAfterReceive(Action action, Ref actionProperty);

}
