package at.irian.ankor.system;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.action.ActionEventListener;
import at.irian.ankor.switching.Switchboard;
import at.irian.ankor.switching.msg.ActionEventMessage;
import at.irian.ankor.switching.party.LocalParty;
import at.irian.ankor.messaging.modify.Modifier;
import at.irian.ankor.switching.party.Party;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.session.ModelSession;

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

    public RemoteNotifyActionEventListener(Switchboard switchboard, Modifier preSendModifier) {
        super(null); //global listener
        this.switchboard = switchboard;
        this.preSendModifier = preSendModifier;
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
            Party sender = new LocalParty(modelSession.getId(), actionProperty.root().propertyName());
            switchboard.send(sender,
                             new ActionEventMessage(actionProperty.path(), modifiedAction));
        }
    }

}
