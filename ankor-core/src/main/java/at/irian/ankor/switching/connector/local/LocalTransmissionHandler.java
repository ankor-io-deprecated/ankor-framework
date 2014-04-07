package at.irian.ankor.switching.connector.local;

import at.irian.ankor.action.Action;
import at.irian.ankor.change.Change;
import at.irian.ankor.event.source.ModelAddressSource;
import at.irian.ankor.event.source.Source;
import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.ref.impl.RefImplementor;
import at.irian.ankor.serialization.modify.Modifier;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.session.ModelSessionManager;
import at.irian.ankor.state.SimpleSendStateDefinition;
import at.irian.ankor.switching.connector.HandlerScopeContext;
import at.irian.ankor.switching.connector.TransmissionHandler;
import at.irian.ankor.switching.msg.ActionEventMessage;
import at.irian.ankor.switching.msg.ChangeEventMessage;
import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.routing.ModelAddress;

import java.util.Map;
import java.util.Set;

/**
 * Delivers received EventMessages to the according local ModelSessions.
 *
 * @author Manfred Geiler
 */
public class LocalTransmissionHandler implements TransmissionHandler<LocalModelAddress> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LocalTransmissionHandler.class);

    private final ModelSessionManager modelSessionManager;
    private final Modifier modifier;

    public LocalTransmissionHandler(ModelSessionManager modelSessionManager, Modifier modifier) {
        this.modelSessionManager = modelSessionManager;
        this.modifier = modifier;
    }

    @Override
    public void transmitEventMessage(ModelAddress sender,
                                     LocalModelAddress receiver,
                                     EventMessage message,
                                     HandlerScopeContext context) {
        LOG.debug("delivering {} from {} to {}", message, sender, receiver);

        String modelSessionId = receiver.getModelSessionId();
        ModelSession modelSession = modelSessionManager.getById(modelSessionId);
        if (modelSession == null) {
            LOG.warn("Model session with id {} does not (or no longer) exist - propably timed out.");
        } else {
            deliver(sender, modelSession, message);
        }
    }

    private void deliver(final ModelAddress sender, final ModelSession modelSession, final EventMessage msg) {

        if (msg instanceof ActionEventMessage) {
            AnkorPatterns.runLater(modelSession, new Runnable() {
                @Override
                public void run() {
                    RefContext refContext = modelSession.getRefContext();
                    RefFactory refFactory = refContext.refFactory();
                    Ref actionProperty = refFactory.ref(((ActionEventMessage) msg).getProperty());
                    Action action = modifier.modifyAfterReceive(((ActionEventMessage) msg).getAction(), actionProperty);
                    applyStateToModelSession(refFactory, ((ActionEventMessage) msg).getStateValues());
                    updateSendStateDefinition(modelSession, ((ActionEventMessage) msg).getStateHolderProperties());
                    Source source = new ModelAddressSource(sender, LocalTransmissionHandler.this);
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
                    RefFactory refFactory = refContext.refFactory();
                    Ref changedProperty = refFactory.ref(((ChangeEventMessage) msg).getProperty());
                    Change change = modifier.modifyAfterReceive(((ChangeEventMessage) msg).getChange(), changedProperty);
                    applyStateToModelSession(refFactory, ((ChangeEventMessage) msg).getStateValues());
                    updateSendStateDefinition(modelSession, ((ChangeEventMessage) msg).getStateHolderProperties());
                    Source source = new ModelAddressSource(sender, LocalTransmissionHandler.this);
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

    private void applyStateToModelSession(RefFactory refFactory, Map<String, Object> stateValues) {
        if (stateValues != null) {
            for (Map.Entry<String, Object> entry : stateValues.entrySet()) {
                //todo we should only allow this if a ref is marked as @StateHolder
                Ref ref = refFactory.ref(entry.getKey());
                ((RefImplementor) ref).internalSetValue(entry.getValue());
            }
        }
    }

    private void updateSendStateDefinition(ModelSession modelSession, Set<String> stateHolderProperties) {
        modelSession.setSendStateDefinition(SimpleSendStateDefinition.of(stateHolderProperties));
    }

}
