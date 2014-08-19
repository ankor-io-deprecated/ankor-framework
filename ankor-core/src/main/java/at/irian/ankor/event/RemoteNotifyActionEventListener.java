package at.irian.ankor.event;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.action.ActionEventListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.serialization.modify.Modifier;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.state.SendStateDefinition;
import at.irian.ankor.switching.Switchboard;
import at.irian.ankor.switching.connector.local.SessionModelAddressBinding;
import at.irian.ankor.switching.msg.ActionEventMessage;
import at.irian.ankor.switching.routing.ModelAddress;

import java.util.Map;
import java.util.Set;

/**
 * Global ActionEventListener that relays locally happened {@link ActionEvent ActionEvents} to all remote systems
 * connected to the underlying ModelSession.
 *
 * @author Manfred Geiler
 */
public class RemoteNotifyActionEventListener extends ActionEventListener {

    private final Switchboard switchboard;
    private final Modifier preSendModifier;
    private final SessionModelAddressBinding sessionModelAddressBinding;

    public RemoteNotifyActionEventListener(Switchboard switchboard,
                                           Modifier preSendModifier) {
        super(null); //global listener
        this.switchboard = switchboard;
        this.preSendModifier = preSendModifier;
        this.sessionModelAddressBinding = new SessionModelAddressBinding();
    }

    @Override
    public boolean isDiscardable() {
        return false; // this is a global system listener
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void process(ActionEvent event) {
        if (Events.isLocalModelEvent(event)) {
            Action action = event.getAction();
            Ref actionProperty = event.getActionProperty();
            Action modifiedAction = preSendModifier.modifyBeforeSend(action, actionProperty);
            ModelSession modelSession = actionProperty.context().modelSession();
            String modelName = actionProperty.root().propertyName();
            ModelAddress sender = sessionModelAddressBinding.getAssociatedModelAddress(modelSession, modelName);
            SendStateDefinition sendStateDefinition = modelSession.getSendStateDefinition();
            Map<String, Object> state = new StateHelper(actionProperty.context().refFactory()).createState(sendStateDefinition);
            Set<String> stateHolderProperties = modelSession.getStateHolderDefinition().getPaths();
            switchboard.send(sender,
                             new ActionEventMessage(actionProperty.path(), modifiedAction, state, stateHolderProperties));
        }
    }

}
