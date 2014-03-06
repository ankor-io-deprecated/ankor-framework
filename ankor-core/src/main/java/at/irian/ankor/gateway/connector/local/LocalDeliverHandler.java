package at.irian.ankor.gateway.connector.local;

import at.irian.ankor.action.Action;
import at.irian.ankor.application.ApplicationInstance;
import at.irian.ankor.change.Change;
import at.irian.ankor.event.source.PartySource;
import at.irian.ankor.gateway.Gateway;
import at.irian.ankor.gateway.handler.DeliverHandler;
import at.irian.ankor.gateway.msg.ActionEventGatewayMsg;
import at.irian.ankor.gateway.msg.ChangeEventGatewayMsg;
import at.irian.ankor.gateway.msg.GatewayMsg;
import at.irian.ankor.gateway.party.LocalParty;
import at.irian.ankor.messaging.modify.Modifier;
import at.irian.ankor.gateway.party.Party;
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
public class LocalDeliverHandler implements DeliverHandler<LocalParty> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LocalDeliverHandler.class);

    private final ModelSessionManager modelSessionManager;
    private final Modifier modifier;
    private final Gateway gateway;

    public LocalDeliverHandler(ModelSessionManager modelSessionManager, Modifier modifier, Gateway gateway) {
        this.modelSessionManager = modelSessionManager;
        this.modifier = modifier;
        this.gateway = gateway;
    }

    @Override
    public void open(Party sender, LocalParty receiver, Map<String, Object> connectParameters) {
        LOG.debug("open connection from {} to {}", sender, receiver);

        String modelName = receiver.getModelName();
        ModelSession modelSession = modelSessionManager.getById(receiver.getModelSessionId());
        ApplicationInstance applicationInstance = modelSession.getApplicationInstance();

        // send an inital change event back to the sender
        Object modelRoot = applicationInstance.getModelRoot(modelName);
        gateway.deliverMessage(receiver, sender,
                               new ChangeEventGatewayMsg(modelName, Change.valueChange(modelRoot)));
    }

    @Override
    public void deliver(Party sender, LocalParty receiver, GatewayMsg message) {
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
    public void close(Party sender, LocalParty receiver) {
        LOG.debug("close connection from {} to {}", sender, receiver);

    }

    private void deliver(final Party sender, final ModelSession modelSession, final GatewayMsg msg) {

        if (msg instanceof ActionEventGatewayMsg) {
            AnkorPatterns.runLater(modelSession, new Runnable() {
                @Override
                public void run() {
                    RefContext refContext = modelSession.getRefContext();
                    Ref actionProperty = refContext.refFactory().ref(((ActionEventGatewayMsg) msg).getProperty());
                    Action action = modifier.modifyAfterReceive(((ActionEventGatewayMsg) msg).getAction(), actionProperty);
                    PartySource source = new PartySource(sender, LocalDeliverHandler.this);
                    ((RefImplementor) actionProperty).fire(source, action);
                }

                @Override
                public String toString() {
                    return "AsyncTask{msg=" + msg + ", modelSession=" + modelSession + "}";
                }
            });

        } else if (msg instanceof ChangeEventGatewayMsg) {
            AnkorPatterns.runLater(modelSession, new Runnable() {
                @Override
                public void run() {
                    RefContext refContext = modelSession.getRefContext();
                    Ref changedProperty = refContext.refFactory().ref(((ChangeEventGatewayMsg) msg).getProperty());
                    Change change = modifier.modifyAfterReceive(((ChangeEventGatewayMsg) msg).getChange(), changedProperty);
                    PartySource source = new PartySource(sender, LocalDeliverHandler.this);
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
