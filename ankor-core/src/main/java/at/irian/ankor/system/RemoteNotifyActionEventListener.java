package at.irian.ankor.system;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.action.ActionEventListener;
import at.irian.ankor.gateway.party.LocalParty;
import at.irian.ankor.messaging.modify.Modifier;
import at.irian.ankor.gateway.msg.ActionEventGatewayMsg;
import at.irian.ankor.gateway.Gateway;
import at.irian.ankor.gateway.party.Party;
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

    private final Gateway gateway;
    private final Modifier preSendModifier;

    public RemoteNotifyActionEventListener(Gateway gateway, Modifier preSendModifier) {
        super(null); //global listener
        this.gateway = gateway;
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
            gateway.routeMessage(sender,
                                 new ActionEventGatewayMsg(actionProperty.path(), modifiedAction));
        }
    }

}
