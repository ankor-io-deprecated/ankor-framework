package at.irian.ankor.gateway.connector.local;

import at.irian.ankor.application.Application;
import at.irian.ankor.application.ApplicationInstance;
import at.irian.ankor.gateway.handler.ConnectHandler;
import at.irian.ankor.gateway.party.LocalParty;
import at.irian.ankor.gateway.party.Party;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.session.ModelSessionManager;

import java.util.Collections;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class LocalConnectHandler implements ConnectHandler {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LocalConnectHandler.class);

    private final ModelSessionManager modelSessionManager;
    private final Application application;

    public LocalConnectHandler(ModelSessionManager modelSessionManager,
                               Application application) {
        this.modelSessionManager = modelSessionManager;
        this.application = application;
    }

    @Override
    public Party findReceiver(Party sender, Map<String, Object> connectParameters) {

        if (connectParameters == null) {
            connectParameters = Collections.emptyMap();
        }
        LOG.info("Connect request received from {} with parameters {}", sender, connectParameters);

        ApplicationInstance applicationInstance = application.getApplicationInstance(connectParameters);
        if (applicationInstance == null) {
            LOG.info("Application '{}' did not accept connect parameters {} and returned a null instance - connection denied",
                     application.getName(), connectParameters);
            return null;
        }

        String modelName = sender.getModelName();

        ModelSession modelSession = modelSessionManager.getOrCreate(applicationInstance);

        Party receiver = new LocalParty(modelSession.getId(), modelName);

        if (receiver.equals(sender)) {
            throw new IllegalArgumentException("ModelSession must not connect to itself: " + modelSession);
        }

        return receiver;
    }
}
