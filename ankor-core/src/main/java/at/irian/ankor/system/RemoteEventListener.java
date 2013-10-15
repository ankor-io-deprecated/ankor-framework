package at.irian.ankor.system;

import at.irian.ankor.action.Action;
import at.irian.ankor.change.Change;
import at.irian.ankor.event.ModelEventListener;
import at.irian.ankor.messaging.ChangeModifier;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.impl.RefBase;

/**
 * ModelEventListener that handles {@link RemoteEvent RemoteEvents}.
 *
 * @author Manfred Geiler
 */
public class RemoteEventListener implements ModelEventListener {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RemoteEventListener.class);

    private final ChangeModifier changeModifier;

    public RemoteEventListener(ChangeModifier changeModifier) {
        this.changeModifier = changeModifier;
    }

    @Override
    public boolean isDiscardable() {
        return false; // this is a global system listener
    }

    public void process(RemoteEvent event) {

        Ref property = event.getSourceProperty();

        Action action = event.getAction();
        if (action != null) {
            property.fire(action);
        }

        Change change = event.getChange();
        if (change != null) {
            change = changeModifier.modify(change, property);
            ((RefBase) property).apply(change);
        }

//        RemoteMissing missing = event.getMissing();
//        if (missing != null) {
//            property.context().modelContext().getEventDispatcher().dispatch(new MissingEvent(property, missing));
//        }
//
    }

}
