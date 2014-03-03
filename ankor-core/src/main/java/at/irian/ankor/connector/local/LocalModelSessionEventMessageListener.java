package at.irian.ankor.connector.local;

import at.irian.ankor.action.Action;
import at.irian.ankor.change.Change;
import at.irian.ankor.event.source.PartySource;
import at.irian.ankor.messaging.modify.Modifier;
import at.irian.ankor.msg.ActionEventMessage;
import at.irian.ankor.msg.ChangeEventMessage;
import at.irian.ankor.msg.EventMessage;
import at.irian.ankor.msg.SwitchingCenter;
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
    private final SwitchingCenter switchingCenter;
    private final Modifier modifier;

    public LocalModelSessionEventMessageListener(ModelSessionManager modelSessionManager,
                                                 SwitchingCenter switchingCenter,
                                                 Modifier modifier) {
        this.modelSessionManager = modelSessionManager;
        this.switchingCenter = switchingCenter;
        this.modifier = modifier;
    }

    @Override
    public void onEventMessage(EventMessage msg) {

        LOG.debug("received {}", msg);

        Party sender = msg.getSender();
        Collection<Party> receivers = switchingCenter.getConnectedParties(sender);

        // todo  handle empty receivers ...

        for (Party receiver : receivers) {

            if (receiver.equals(sender)) {
                LOG.error("Self-connected sender: {}", sender);
                continue;
            }

            if (receiver instanceof LocalModelSessionParty) {
                String modelSessionId = ((LocalModelSessionParty) receiver).getModelSessionId();
                ModelSession modelSession = modelSessionManager.getById(modelSessionId);
                if (modelSession == null) {
                    LOG.warn("Model session with id {} does not (or no longer) exist - propably timed out.");
                } else {
                    fireEvent(modelSession, msg);
                }
            }
        }

    }

    private void fireEvent(final ModelSession modelSession, final EventMessage msg) {


        if (msg instanceof ActionEventMessage) {
            AnkorPatterns.runLater(modelSession, new Runnable() {
                @Override
                public void run() {
                    RefContext refContext = modelSession.getRefContext();
                    Ref actionProperty = refContext.refFactory().ref(((ActionEventMessage) msg).getProperty());
                    Action action = modifier.modifyAfterReceive(((ActionEventMessage) msg).getAction(), actionProperty);
                    ((RefImplementor)actionProperty).fire(new PartySource(msg.getSender()), action);
                }
            });

        } else if (msg instanceof ChangeEventMessage) {
            AnkorPatterns.runLater(modelSession, new Runnable() {
                @Override
                public void run() {
                    RefContext refContext = modelSession.getRefContext();
                    Ref changedProperty = refContext.refFactory().ref(((ChangeEventMessage) msg).getProperty());
                    Change change = modifier.modifyAfterReceive(((ChangeEventMessage) msg).getChange(), changedProperty);
                    ((RefImplementor)changedProperty).apply(new PartySource(msg.getSender()), change);
                }
            });

        } else {

            throw new IllegalArgumentException("Unsupported message type " + msg.getClass().getName());

        }
    }

}
