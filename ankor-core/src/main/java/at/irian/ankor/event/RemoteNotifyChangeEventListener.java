package at.irian.ankor.event;

import at.irian.ankor.change.Change;
import at.irian.ankor.change.ChangeEvent;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.serialization.modify.Modifier;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.state.SendStateDefinition;
import at.irian.ankor.switching.Switchboard;
import at.irian.ankor.switching.connector.local.SessionModelAddressBinding;
import at.irian.ankor.switching.msg.ChangeEventMessage;
import at.irian.ankor.switching.routing.ModelAddress;

import java.util.Map;
import java.util.Set;

/**
 * Global ChangeEventListener that relays locally happened {@link ChangeEvent ChangeEvents} to all remote systems
 * connected to the underlying ModelSession.
 *
 * @author Manfred Geiler
 */
public class RemoteNotifyChangeEventListener extends ChangeEventListener {

    private final Switchboard switchboard;
    private final Modifier preSendModifier;
    private final SessionModelAddressBinding sessionModelAddressBinding;

    public RemoteNotifyChangeEventListener(Switchboard switchboard,
                                           Modifier preSendModifier,
                                           SessionModelAddressBinding sessionModelAddressBinding) {
        super(null); //global listener
        this.switchboard = switchboard;
        this.preSendModifier = preSendModifier;
        this.sessionModelAddressBinding = sessionModelAddressBinding;
    }

    @Override
    public boolean isDiscardable() {
        return false; // this is a global system listener
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void process(ChangeEvent event) {
        if (Events.isLocalModelEvent(event)) {
            Change change = event.getChange();
            Ref changedProperty = event.getProperty();
            Change modifiedChange = preSendModifier.modifyBeforeSend(change, changedProperty);
            ModelSession modelSession = changedProperty.context().modelSession();
            String modelName = changedProperty.root().propertyName();
            ModelAddress sender = sessionModelAddressBinding.getAssociatedModelAddress(modelSession, modelName);
            SendStateDefinition sendStateDefinition = modelSession.getSendStateDefinition();
            Map<String, Object> state = new StateHelper(changedProperty.context().refFactory()).createState(sendStateDefinition);
            Set<String> stateHolderProperties = modelSession.getStateHolderDefinition().getPaths();
            switchboard.send(sender,
                             new ChangeEventMessage(changedProperty.path(), modifiedChange, state, stateHolderProperties));
        }
    }

}
