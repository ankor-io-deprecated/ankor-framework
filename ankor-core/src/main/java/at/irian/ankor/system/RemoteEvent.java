package at.irian.ankor.system;

import at.irian.ankor.event.ModelEvent;
import at.irian.ankor.event.ModelEventListener;
import at.irian.ankor.ref.Ref;

import java.util.Map;

/**
 * A RemoteEvent is the instantiation of a ModelEvent that happened on the remote side and was transferred
 * via messaging to the local system.
 *
 * The RemoteEvent acts as a "circuit breaker" that defines special {@link Action} and {@link Change} types.
 * These special types are well-known to the relaying event listeners ({@link DefaultSyncActionEventListener}
 * and {@link DefaultSyncChangeEventListener}), which are hence able to detect if an event was just received
 * from the remote system. This way it is guaranteed that remote events are not relayed back to the remote system,
 * which would of course lead to an immediate short-circuit.
 *
 * @author Manfred Geiler
 */
public class RemoteEvent extends ModelEvent {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RemoteEvent.class);

    private final Change change;
    private final Action action;

    private RemoteEvent(Ref changedProperty, Change change) {
        super(changedProperty);
        this.change = change;
        this.action = null;
    }

    private RemoteEvent(Ref source, Action action) {
        super(source);
        this.change = null;
        this.action = action;
    }

    public static RemoteEvent createChangeEvent(Ref changedProperty, Object newValue) {
        return new RemoteEvent(changedProperty, new Change(newValue));
    }

    public static RemoteEvent createActionEvent(Ref actionProperty, String actionName, Map<String, Object> actionParams) {
        return new RemoteEvent(actionProperty, new Action(actionName, actionParams));
    }

    public Change getChange() {
        return change;
    }

    public Action getAction() {
        return action;
    }

    @Override
    public boolean isAppropriateListener(ModelEventListener listener) {
        return listener instanceof RemoteEventListener;
    }

    @Override
    public void processBy(ModelEventListener listener) {
        ((RemoteEventListener)listener).process(this);
    }

    public static class Action extends at.irian.ankor.action.Action {
        private Action(String name, Map<String, Object> params) {
            super(name, params);
        }
    }

    public static class Change extends at.irian.ankor.change.Change {
        private Change(Object newValue) {
            super(newValue);
        }
    }
}
