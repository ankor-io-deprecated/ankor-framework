package at.irian.ankor.connector.local;

import at.irian.ankor.msg.CloseMessage;
import at.irian.ankor.msg.OrphanMessage;
import at.irian.ankor.msg.party.Party;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.session.ModelSessionManager;

/**
 * @author Manfred Geiler
 */
class LocalModelSessionCloseMessageListener implements CloseMessage.Listener, OrphanMessage.Listener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LocalModelSessionCloseMessageListener.class);

    private final ModelSessionManager modelSessionManager;

    public LocalModelSessionCloseMessageListener(ModelSessionManager modelSessionManager) {
        this.modelSessionManager = modelSessionManager;
    }

    @Override
    public void onCloseMessage(CloseMessage msg) {
        Party party = msg.getPartyToClose();
        if (party instanceof LocalModelSessionParty) {
            closeSession((LocalModelSessionParty) party);
        }
    }

    @Override
    public void onOrphanMessage(OrphanMessage msg) {
        Party party = msg.getOrphanedParty();
        if (party instanceof LocalModelSessionParty) {
            closeSession((LocalModelSessionParty) party);
        }
    }

    private void closeSession(LocalModelSessionParty party) {
        String modelSessionId = party.getModelSessionId();
        ModelSession modelSession = modelSessionManager.getById(modelSessionId);
        if (modelSession != null) {
            LOG.info("Invalidating orphaned model session with id {}", modelSessionId);
            modelSessionManager.invalidate(modelSession);
        }
    }

}
