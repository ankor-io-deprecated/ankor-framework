package at.irian.ankor.connector.local;

import at.irian.ankor.application.Application;
import at.irian.ankor.application.ApplicationInstance;
import at.irian.ankor.msg.ConnectMessage;
import at.irian.ankor.msg.SwitchingCenter;
import at.irian.ankor.msg.party.Party;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.session.ModelSessionManager;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
class LocalModelSessionConnectMessageListener implements ConnectMessage.Listener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LocalModelSessionConnectMessageListener.class);

    private final ModelSessionManager modelSessionManager;
    private final SwitchingCenter switchingCenter;
    private final Application application;

    public LocalModelSessionConnectMessageListener(ModelSessionManager modelSessionManager,
                                                   SwitchingCenter switchingCenter,
                                                   Application application) {
        this.modelSessionManager = modelSessionManager;
        this.switchingCenter = switchingCenter;
        this.application = application;
    }

    @Override
    public void onConnectMessage(ConnectMessage msg) {

        Party sender = msg.getSender();
        Map<String,Object> connectParameters = msg.getConnectParameters();

        LOG.info("Connect message received from {} with parameters {}", sender, connectParameters);

        ApplicationInstance applicationInstance = application.getApplicationInstance(connectParameters);
        if (applicationInstance == null) {
            LOG.warn("Application {} returned a null instance for connect parameters {}", application.getName(),
                     connectParameters);
            return;
        }

        ModelSession modelSession = modelSessionManager.getOrCreate(applicationInstance);

        Party receiver = new LocalModelSessionParty(modelSession.getId());

        if (receiver.equals(sender)) {
            throw new IllegalArgumentException("ModelSession must not connect to itself: " + modelSession);
        }

        if (switchingCenter.isConnected(sender, receiver)) {
            LOG.warn("Already connected: {} and {}", sender, receiver);
        } else {
            LOG.debug("Connecting {} and {}", sender, receiver);
            switchingCenter.connect(sender, receiver);
        }


        // todo  broadcast  "connected ok" message
        // todo  initiate "root change event"

    }
}
