package at.irian.ankor.event;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.action.ActionEventListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.serialization.modify.Modifier;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.state.StateDefinition;
import at.irian.ankor.switching.Switchboard;
import at.irian.ankor.switching.connector.local.LocalModelAddress;
import at.irian.ankor.switching.msg.ActionEventMessage;
import at.irian.ankor.switching.routing.ModelAddress;

import java.util.Map;

/**
 * Global ActionEventListener that relays locally happened {@link ActionEvent ActionEvents} to all remote systems
 * connected to the underlying ModelSession.
 *
 * @author Manfred Geiler
 */
public class RemoteNotifyActionEventListener extends ActionEventListener {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RemoteNotifyActionEventListener.class);

    private final Switchboard switchboard;
    private final Modifier preSendModifier;
    private final StateDefinition stateDefinition;

    public RemoteNotifyActionEventListener(Switchboard switchboard,
                                           Modifier preSendModifier,
                                           StateDefinition stateDefinition) {
        super(null); //global listener
        this.switchboard = switchboard;
        this.preSendModifier = preSendModifier;
        this.stateDefinition = stateDefinition;
    }

    @Override
    public boolean isDiscardable() {
        return false; // this is a global system listener
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void process(ActionEvent event) {
        if (event.isLocalEvent()) {
            Action action = event.getAction();
            Ref actionProperty = event.getActionProperty();
            Action modifiedAction = preSendModifier.modifyBeforeSend(action, actionProperty);
            ModelSession modelSession = actionProperty.context().modelSession();
            ModelAddress sender = new LocalModelAddress(modelSession, actionProperty.root().propertyName());
            Map<String, Object> state = new StateHelper(actionProperty.context().refFactory()).createState(stateDefinition);
            switchboard.send(sender,
                             new ActionEventMessage(actionProperty.path(), modifiedAction, state));
        }
    }

}
