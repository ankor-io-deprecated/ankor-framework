package at.irian.ankor.connector.local;

import at.irian.ankor.msg.CloseRequestMessage;
import at.irian.ankor.msg.party.Party;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.session.ModelSessionManager;

/**
 * @author Manfred Geiler
 */
class LocalCloseRequestMessageListener implements CloseRequestMessage.Listener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LocalCloseRequestMessageListener.class);

    private final ModelSessionManager modelSessionManager;

    public LocalCloseRequestMessageListener(ModelSessionManager modelSessionManager) {
        this.modelSessionManager = modelSessionManager;
    }

    @Override
    public void onCloseMessage(CloseRequestMessage msg) {
        Party party = msg.getPartyToClose();
        if (party instanceof LocalParty) {
            closeSession((LocalParty) party, msg.getSender());
        }
    }

    private void closeSession(LocalParty party, Party sender) {
        String modelSessionId = party.getModelSessionId();
        LOG.info("Closing model session with id {} as requested by {}", modelSessionId, sender);
        ModelSession modelSession = modelSessionManager.getById(modelSessionId);
        if (modelSession != null) {
            modelSessionManager.invalidate(modelSession);
        } else {
            LOG.info("Model session with id {} does not exist - propably timed out", modelSessionId);
        }
    }

}
