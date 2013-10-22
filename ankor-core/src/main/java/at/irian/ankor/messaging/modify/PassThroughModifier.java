package at.irian.ankor.messaging.modify;

import at.irian.ankor.action.Action;
import at.irian.ankor.change.Change;
import at.irian.ankor.ref.Ref;

/**
* @author Manfred Geiler
*/
public class PassThroughModifier implements Modifier {

    @Override
    public Change modifyBeforeSend(Change change, Ref changedProperty) {
        return change;
    }

    @Override
    public Change modifyAfterReceive(Change change, Ref changedProperty) {
        return change;
    }

    @Override
    public Action modifyBeforeSend(Action action, Ref changedProperty) {
        return action;
    }

    @Override
    public Action modifyAfterReceive(Action action, Ref changedProperty) {
        return action;
    }
}
