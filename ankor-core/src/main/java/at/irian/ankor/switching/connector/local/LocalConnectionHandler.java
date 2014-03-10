package at.irian.ankor.switching.connector.local;

import at.irian.ankor.change.Change;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.session.ModelSessionManager;
import at.irian.ankor.switching.Switchboard;
import at.irian.ankor.switching.connector.ConnectionHandler;
import at.irian.ankor.switching.msg.ChangeEventMessage;
import at.irian.ankor.switching.party.LocalParty;
import at.irian.ankor.switching.party.Party;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class LocalConnectionHandler implements ConnectionHandler<LocalParty> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LocalConnectionHandler.class);

    private final ModelSessionManager modelSessionManager;
    private final Switchboard switchboard;

    public LocalConnectionHandler(ModelSessionManager modelSessionManager,
                                  Switchboard switchboard) {
        this.modelSessionManager = modelSessionManager;
        this.switchboard = switchboard;
    }

    @Override
    public void openConnection(Party sender, LocalParty receiver, Map<String, Object> connectParameters) {
        LOG.debug("open connection from {} to {}", sender, receiver);

        String modelName = receiver.getModelName();
        ModelSession modelSession = modelSessionManager.getById(receiver.getModelSessionId());

        // send an inital change event for the model root back to the sender
        Object modelRoot = modelSession.getModelRoot(modelName);
        switchboard.send(receiver, sender,
                         new ChangeEventMessage(modelName, Change.valueChange(modelRoot)));
    }

    @Override
    public void closeConnection(Party sender, LocalParty receiver, boolean lastRoute) {
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
