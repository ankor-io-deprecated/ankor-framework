package at.irian.ankor.system;

import at.irian.ankor.action.RemoteAction;
import at.irian.ankor.change.RemoteChange;
import at.irian.ankor.event.ModelEvent;
import at.irian.ankor.event.ModelEventListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.session.Session;

import java.util.Map;

/**
 * A RemoteEvent is the instantiation of a ModelEvent that happened on the remote side and was transferred
 * via messaging to the local system.
 *
 * The RemoteEvent acts as a "circuit breaker" that uses special {@link at.irian.ankor.action.RemoteAction}
 * and {@link at.irian.ankor.change.RemoteChange} types.
 * These special types are well-known to the relaying event listeners ({@link RemoteNotifyActionEventListener}
 * and {@link RemoteNotifyChangeEventListener}), which are hence able to detect if an event was just received
 * from the remote system. This way it is guaranteed that remote events are not relayed back to the remote system,
 * which would of course lead to an immediate short-circuit.
 *
 * @author Manfred Geiler
 */
public class RemoteEvent extends ModelEvent {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RemoteEvent.class);

    private final RemoteChange change;
    private final RemoteAction action;

    private RemoteEvent(Ref changedProperty, RemoteChange change) {
        super(changedProperty);
        this.change = change;
        this.action = null;
    }

    private RemoteEvent(Ref source, RemoteAction action) {
        super(source);
        this.change = null;
        this.action = action;
    }

    public static RemoteEvent createChangeEvent(Session session, Ref changedProperty, Object newValue) {
        return new RemoteEvent(changedProperty, new RemoteChange(session, newValue));
    }

    public static RemoteEvent createActionEvent(Session session, Ref actionProperty, String actionName, Map<String, Object> actionParams) {
        return new RemoteEvent(actionProperty, new RemoteAction(session, actionName, actionParams));
    }

    public RemoteChange getChange() {
        return change;
    }

    public RemoteAction getAction() {
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

}
