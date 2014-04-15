package at.irian.ankor.switching.connector.local;

import at.irian.ankor.serialization.modify.Modifier;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.session.ModelSessionManager;
import at.irian.ankor.switching.connector.HandlerScopeContext;
import at.irian.ankor.switching.connector.TransmissionHandler;
import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.routing.ModelAddress;

/**
 * Delivers received EventMessages to the according local ModelSessions.
 *
 * @author Manfred Geiler
 */
public class StatefulSessionTransmissionHandler implements TransmissionHandler<StatefulSessionModelAddress> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(StatefulSessionTransmissionHandler.class);

    private final ModelSessionManager modelSessionManager;
    private final DeliverHelper deliverHelper;

    public StatefulSessionTransmissionHandler(ModelSessionManager modelSessionManager, Modifier modifier) {
        this.modelSessionManager = modelSessionManager;
        this.deliverHelper = new DeliverHelper(modifier);
    }

    @Override
    public void transmitEventMessage(ModelAddress sender,
                                     StatefulSessionModelAddress receiver,
                                     EventMessage message,
                                     HandlerScopeContext context) {
        LOG.debug("delivering {} from {} to {}", message, sender, receiver);

        String modelSessionId = receiver.getModelSessionId();
        ModelSession modelSession = modelSessionManager.getById(modelSessionId);
        if (modelSession == null) {
            LOG.warn("Model session with id {} does not (or no longer) exist - propably timed out.");
        } else {
            deliverHelper.deliver(sender, modelSession, message);
        }
    }

}
