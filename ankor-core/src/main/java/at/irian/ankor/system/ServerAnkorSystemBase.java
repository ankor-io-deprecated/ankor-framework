package at.irian.ankor.system;

import at.irian.ankor.messaging.MessageBus;
import at.irian.ankor.messaging.MessageFactory;
import at.irian.ankor.ref.RefContextFactory;
import at.irian.ankor.session.SessionManager;

/**
 * @author Manfred Geiler
 */
public abstract class ServerAnkorSystemBase extends AnkorSystem {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ServerAnkorSystemBase.class);

    protected ServerAnkorSystemBase(String systemName,
                                    MessageFactory messageFactory,
                                    MessageBus messageBus,
                                    RefContextFactory refContextFactory,
                                    SessionManager sessionManager) {
        super(systemName, messageFactory, messageBus, refContextFactory, sessionManager);
    }

}
