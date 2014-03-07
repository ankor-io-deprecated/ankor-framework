package at.irian.ankor.switching.connector.local;

import at.irian.ankor.action.Action;
import at.irian.ankor.application.ApplicationInstance;
import at.irian.ankor.change.Change;
import at.irian.ankor.event.source.PartySource;
import at.irian.ankor.switching.Switchboard;
import at.irian.ankor.switching.handler.SendHandler;
import at.irian.ankor.switching.msg.ActionEventMessage;
import at.irian.ankor.switching.msg.ChangeEventMessage;
import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.party.LocalParty;
import at.irian.ankor.messaging.modify.Modifier;
import at.irian.ankor.switching.party.Party;
import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.impl.RefImplementor;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.session.ModelSessionManager;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class LocalSendHandler implements SendHandler<LocalParty> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LocalSendHandler.class);

    private final ModelSessionManager modelSessionManager;
    private final Modifier modifier;
    private final Switchboard switchboard;

    public LocalSendHandler(ModelSessionManager modelSessionManager, Modifier modifier, Switchboard switchboard) {
        this.modelSessionManager = modelSessionManager;
        this.modifier = modifier;
        this.switchboard = switchboard;
    }

    @Override
    public void deliverConnectRequest(Party sender, LocalParty receiver, Map<String, Object> connectParameters) {
        LOG.debug("open connection from {} to {}", sender, receiver);

        String modelName = receiver.getModelName();
        ModelSession modelSession = modelSessionManager.getById(receiver.getModelSessionId());
        ApplicationInstance applicationInstance = modelSession.getApplicationInstance();

        // send an inital change event back to the sender
        Object modelRoot = applicationInstance.getModelRoot(modelName);
        switchboard.deliver(receiver, sender,
                            new ChangeEventMessage(modelName, Change.valueChange(modelRoot)));
    }

    @Override
    public void deliverEventMessage(Party sender, LocalParty receiver, EventMessage message) {
        LOG.debug("delivering {} from {} to {}", message, sender, receiver);

        String modelSessionId = receiver.getModelSessionId();
        ModelSession modelSession = modelSessionManager.getById(modelSessionId);
        if (modelSession == null) {
            LOG.warn("Model session with id {} does not (or no longer) exist - propably timed out.");
        } else {
            deliver(sender, modelSession, message);
        }
    }

    @Override
    public void deliverCloseRequest(Party sender, LocalParty receiver) {
        LOG.debug("close connection from {} to {}", sender, receiver);

    }

    private void deliver(final Party sender, final ModelSession modelSession, final EventMessage msg) {

        if (msg instanceof ActionEventMessage) {
            AnkorPatterns.runLater(modelSession, new Runnable() {
                @Override
                public void run() {
                    RefContext refContext = modelSession.getRefContext();
                    Ref actionProperty = refContext.refFactory().ref(((ActionEventMessage) msg).getProperty());
                    Action action = modifier.modifyAfterReceive(((ActionEventMessage) msg).getAction(), actionProperty);
                    PartySource source = new PartySource(sender, LocalSendHandler.this);
                    ((RefImplementor) actionProperty).fire(source, action);
                }

                @Override
                public String toString() {
                    return "AsyncTask{msg=" + msg + ", modelSession=" + modelSession + "}";
                }
            });

        } else if (msg instanceof ChangeEventMessage) {
            AnkorPatterns.runLater(modelSession, new Runnable() {
                @Override
                public void run() {
                    RefContext refContext = modelSession.getRefContext();
                    Ref changedProperty = refContext.refFactory().ref(((ChangeEventMessage) msg).getProperty());
                    Change change = modifier.modifyAfterReceive(((ChangeEventMessage) msg).getChange(), changedProperty);
                    PartySource source = new PartySource(sender, LocalSendHandler.this);
                    ((RefImplementor)changedProperty).apply(source, change);
                }

                @Override
                public String toString() {
                    return "AsyncTask{msg=" + msg + ", modelSession=" + modelSession + "}";
                }
            });

        } else {

            throw new IllegalArgumentException("Unsupported message type " + msg.getClass().getName());

        }
    }

}
