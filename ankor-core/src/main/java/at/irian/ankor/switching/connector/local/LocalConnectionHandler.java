package at.irian.ankor.switching.connector.local;

import at.irian.ankor.change.Change;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.session.ModelSessionManager;
import at.irian.ankor.switching.Switchboard;
import at.irian.ankor.switching.connector.ConnectionHandler;
import at.irian.ankor.switching.connector.HandlerScopeContext;
import at.irian.ankor.switching.msg.ChangeEventMessage;
import at.irian.ankor.switching.routing.ModelAddress;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class LocalConnectionHandler implements ConnectionHandler<LocalModelAddress> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LocalConnectionHandler.class);

    private final ModelSessionManager modelSessionManager;
    private final Switchboard switchboard;

    public LocalConnectionHandler(ModelSessionManager modelSessionManager,
                                  Switchboard switchboard) {
        this.modelSessionManager = modelSessionManager;
        this.switchboard = switchboard;
    }

    @Override
    public void openConnection(ModelAddress sender,
                               LocalModelAddress receiver,
                               Map<String, Object> connectParameters,
                               HandlerScopeContext context) {
        LOG.debug("open connection from {} to {}", sender, receiver);

        String modelName = receiver.getModelName();
        String modelSessionId = receiver.getModelSessionId();
        ModelSession modelSession = modelSessionManager.getById(modelSessionId);
        if (modelSession == null) {
            throw new IllegalStateException("ModelSession with id " + modelSessionId + " not found - propably timed out");
        }

        // send an initial change event for the model root back to the sender
        Object modelRoot = modelSession.getModelRoot(modelName);
        Map<String, Object> state = null; // todo
        switchboard.send(receiver, new ChangeEventMessage(modelName,
                                                          Change.valueChange(modelRoot),
                                                          state,
                                                          modelSession.getStateHolderDefinition().getPaths()), sender);
    }

    @Override
    public void closeConnection(ModelAddress sender,
                                LocalModelAddress receiver,
                                boolean lastRoute,
                                HandlerScopeContext context) {
        if (lastRoute) {
            String modelSessionId = receiver.getModelSessionId();
            ModelSession modelSession = modelSessionManager.getById(modelSessionId);
            if (modelSession != null) {
                LOG.info("Closing model session with id {}", modelSessionId);

                modelSession.close();
                modelSessionManager.remove(modelSession);

            } else {
                LOG.info("Model session with id {} does not exist - propably timed out", modelSessionId);
            }
        }
    }
}
