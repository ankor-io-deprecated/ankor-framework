package at.irian.ankor.system;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.action.ActionEventListener;
import at.irian.ankor.messaging.modify.Modifier;
import at.irian.ankor.msg.ActionEventMessage;
import at.irian.ankor.msg.MessageBus;
import at.irian.ankor.connector.local.LocalModelSessionParty;
import at.irian.ankor.msg.party.Party;
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

    private final MessageBus messageBus;
    private final Modifier preSendModifier;

    public RemoteNotifyActionEventListener(MessageBus messageBus, Modifier preSendModifier) {
        super(null); //global listener
        this.messageBus = messageBus;
        this.preSendModifier = preSendModifier;
    }

    @Override
    public boolean isDiscardable() {
        return false; // this is a global system listener
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void process(ActionEvent event) {
        Action action = event.getAction();
        Ref actionProperty = event.getActionProperty();
        Action modifiedAction = preSendModifier.modifyBeforeSend(action, actionProperty);
        ModelSession modelSession = actionProperty.context().modelSession();
        Party sender = new LocalModelSessionParty(modelSession.getId());
        messageBus.broadcast(new ActionEventMessage(sender, actionProperty.path(), modifiedAction));
    }
}
