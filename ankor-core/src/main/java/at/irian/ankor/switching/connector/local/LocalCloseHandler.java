package at.irian.ankor.switching.connector.local;

import at.irian.ankor.switching.handler.CloseHandler;
import at.irian.ankor.switching.party.LocalParty;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.session.ModelSessionManager;

/**
 * @author Manfred Geiler
 */
public class LocalCloseHandler implements CloseHandler<LocalParty> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LocalCloseHandler.class);

    private final ModelSessionManager modelSessionManager;

    public LocalCloseHandler(ModelSessionManager modelSessionManager) {
        this.modelSessionManager = modelSessionManager;
    }

    @Override
    public void closeConnector(LocalParty party) {
        String modelSessionId = party.getModelSessionId();
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
