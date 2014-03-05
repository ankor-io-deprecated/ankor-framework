package at.irian.ankor.connector.local;

import at.irian.ankor.action.Action;
import at.irian.ankor.change.Change;
import at.irian.ankor.event.source.PartySource;
import at.irian.ankor.messaging.modify.Modifier;
import at.irian.ankor.msg.*;
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
class LocalModelSessionEventMessageListener implements EventMessage.Listener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LocalModelSessionEventMessageListener.class);

    private final ModelSessionManager modelSessionManager;
    private final RoutingTable routingTable;
    private final Modifier modifier;
    private final MessageBus messageBus;

    public LocalModelSessionEventMessageListener(ModelSessionManager modelSessionManager,
                                                 RoutingTable routingTable,
                                                 Modifier modifier,
                                                 MessageBus messageBus) {
        this.modelSessionManager = modelSessionManager;
        this.routingTable = routingTable;
        this.modifier = modifier;
        this.messageBus = messageBus;
    }

    @Override
    public void onEventMessage(EventMessage msg) {

        Party sender = msg.getSender();
        Collection<Party> receivers = routingTable.getConnectedParties(sender);

        boolean anyLocalReceiver = false;
        for (Party receiver : receivers) {

            if (receiver.equals(sender)) {
                LOG.error("Self-connected sender detected: {}", sender);
                continue;
            }

            if (msg.getEventSource() instanceof PartySource) {
                Party eventSourceParty = ((PartySource) msg.getEventSource()).getParty();
                if (receiver.equals(eventSourceParty)) {
                    LOG.error("Circular routing detected: {}", receiver);
                    continue;
                }
            }

            if (receiver instanceof LocalModelSessionParty) {
                anyLocalReceiver = true;

                LOG.debug("received {} for {}", msg, receiver);

                String modelSessionId = ((LocalModelSessionParty) receiver).getModelSessionId();
                ModelSession modelSession = modelSessionManager.getById(modelSessionId);
                if (modelSession == null) {
                    LOG.warn("Model session with id {} does not (or no longer) exist - propably timed out.");
                } else {
                    // handle event locally ...
                    handleEventMsg(modelSession, msg);

                    // ... and propagate it to other connected parties
                    forwardToOtherConnectedParties(msg, sender, receiver);
                }
            }
        }

        if (!(sender instanceof LocalModelSessionParty) && !anyLocalReceiver) {
            // this was a message from an "external" connector but no local connected session was found...
            LOG.warn("Unhandled external message {} - no appropriate ModelSession found", msg);
        }
    }

    private void forwardToOtherConnectedParties(EventMessage msg, Party originalSender, Party localModelParty) {
        Collection<Party> otherParties = routingTable.getConnectedParties(localModelParty);
        for (Party otherParty : otherParties) {
            if (otherParty.equals(originalSender)) {
                // do not relay back to original sender
            } else {
                LOG.debug("");
                messageBus.broadcast(msg.withSender(localModelParty));
            }
        }
    }


    private void handleEventMsg(final ModelSession modelSession, final EventMessage msg) {

        if (msg instanceof ActionEventMessage) {
            AnkorPatterns.runLater(modelSession, new Runnable() {
                @Override
                public void run() {
                    RefContext refContext = modelSession.getRefContext();
                    Ref actionProperty = refContext.refFactory().ref(((ActionEventMessage) msg).getProperty());
                    Action action = modifier.modifyAfterReceive(((ActionEventMessage) msg).getAction(), actionProperty);
                    PartySource source = new PartySource(msg.getSender(), LocalModelSessionEventMessageListener.this);
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
                    PartySource source = new PartySource(msg.getSender(), LocalModelSessionEventMessageListener.this);
                    ((RefImplementor)changedProperty).apply(source, change);
                }
            });

        } else {

            throw new IllegalArgumentException("Unsupported message type " + msg.getClass().getName());

        }
    }

}
