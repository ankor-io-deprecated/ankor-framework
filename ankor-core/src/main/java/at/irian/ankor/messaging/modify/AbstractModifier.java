package at.irian.ankor.messaging.modify;

import at.irian.ankor.action.Action;
import at.irian.ankor.change.Change;
import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public abstract class AbstractModifier implements Modifier {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AbstractModifier.class);

    private final Modifier parent;

    protected AbstractModifier(Modifier parent) {
        this.parent = parent;
    }

    @Override
    public Change modifyBeforeSend(Change change, Ref changedProperty) {
        return parent.modifyBeforeSend(change, changedProperty);
    }

    @Override
    public Change modifyAfterReceive(Change change, Ref changedProperty) {
        return parent.modifyAfterReceive(change, changedProperty);
    }

    @Override
    public Action modifyBeforeSend(Action action, Ref actionProperty) {
        return parent.modifyBeforeSend(action, actionProperty);
    }

    @Override
    public Action modifyAfterReceive(Action action, Ref actionProperty) {
        return parent.modifyAfterReceive(action, actionProperty);
    }
}
