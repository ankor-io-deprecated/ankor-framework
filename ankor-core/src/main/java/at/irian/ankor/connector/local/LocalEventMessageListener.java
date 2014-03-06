package at.irian.ankor.connector.local;

import at.irian.ankor.action.Action;
import at.irian.ankor.change.Change;
import at.irian.ankor.event.source.PartySource;
import at.irian.ankor.messaging.modify.Modifier;
import at.irian.ankor.msg.AbstractEventMessage;
import at.irian.ankor.msg.ActionEventMessage;
import at.irian.ankor.msg.ChangeEventMessage;
import at.irian.ankor.msg.RoutingTable;
import at.irian.ankor.msg.party.Party;
import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.impl.RefImplementor;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.session.ModelSessionManager;

import java.util.Collection;

/**
 * @author Manfred Geiler
 */
class LocalEventMessageListener implements AbstractEventMessage.Listener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LocalEventMessageListener.class);

    private final ModelSessionManager modelSessionManager;
    private final RoutingTable routingTable;
    private final Modifier modifier;

    public LocalEventMessageListener(ModelSessionManager modelSessionManager,
                                     RoutingTable routingTable,
                                     Modifier modifier) {
        this.modelSessionManager = modelSessionManager;
        this.routingTable = routingTable;
        this.modifier = modifier;
    }

    @Override
    public void onEventMessage(AbstractEventMessage msg) {

        Party sender = msg.getSender();
        Collection<Party> receivers = routingTable.getConnectedParties(sender);

        boolean anyLocalReceiver = false;
        for (Party receiver : receivers) {

            if (receiver.equals(sender)) {
                LOG.error("Self-connected sender detected: {} - ignoring message {}", sender, msg);
                continue;
            }

            if (msg.getEventSource() instanceof PartySource) {
                Party eventSourceParty = ((PartySource) msg.getEventSource()).getParty();
                if (receiver.equals(eventSourceParty)) {
                    LOG.error("Circular routing detected: {} - ignoring message {}", receiver, msg);
                    continue;
                }
            }

            if (receiver instanceof LocalParty) {
                anyLocalReceiver = true;

                LOG.debug("received {} for {}", msg, receiver);

                String modelSessionId = ((LocalParty) receiver).getModelSessionId();
                ModelSession modelSession = modelSessionManager.getById(modelSessionId);
                if (modelSession == null) {
                    LOG.warn("Model session with id {} does not (or no longer) exist - propably timed out.");
                } else {
                    handleEventMsg(modelSession, msg);
                }
            }
        }

        if (!(sender instanceof LocalParty) && !anyLocalReceiver) {
            // this was a message from an "external" connector but no local connected session was found...
            LOG.warn("Unhandled external message {} - no appropriate ModelSession found", msg);
        }
    }

    private void handleEventMsg(final ModelSession modelSession, final AbstractEventMessage msg) {

        if (msg instanceof ActionEventMessage) {
            AnkorPatterns.runLater(modelSession, new Runnable() {
                @Override
                public void run() {
                    RefContext refContext = modelSession.getRefContext();
                    Ref actionProperty = refContext.refFactory().ref(((ActionEventMessage) msg).getProperty());
                    Action action = modifier.modifyAfterReceive(((ActionEventMessage) msg).getAction(), actionProperty);
                    PartySource source = new PartySource(msg.getSender(), LocalEventMessageListener.this);
                    ((RefImplementor)actionProperty).fire(source, action);
                }
            });

        } else if (msg instanceof ChangeEventMessage) {
            AnkorPatterns.runLater(modelSession, new Runnable() {
                @Override
                public void run() {
                    RefContext refContext = modelSession.getRefContext();
                    Ref changedProperty = refContext.refFactory().ref(((ChangeEventMessage) msg).getProperty());
                    Change change = modifier.modifyAfterReceive(((ChangeEventMessage) msg).getChange(), changedProperty);
                    PartySource source = new PartySource(msg.getSender(), LocalEventMessageListener.this);
                    ((RefImplementor)changedProperty).apply(source, change);
                }
            });

        } else {

            throw new IllegalArgumentException("Unsupported message type " + msg.getClass().getName());

        }
    }

}
