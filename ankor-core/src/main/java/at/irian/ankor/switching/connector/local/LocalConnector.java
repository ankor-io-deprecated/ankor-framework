package at.irian.ankor.switching.connector.local;

import at.irian.ankor.messaging.modify.Modifier;
import at.irian.ankor.session.ModelSessionManager;
import at.irian.ankor.switching.Switchboard;
import at.irian.ankor.switching.connector.Connector;
import at.irian.ankor.switching.connector.ConnectorPlug;
import at.irian.ankor.switching.party.LocalParty;
import at.irian.ankor.system.AnkorSystem;

/**
 * @author Manfred Geiler
 */
@SuppressWarnings("UnusedDeclaration")  // indirectly called by ServiceLoader
public class LocalConnector implements Connector {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LocalConnector.class);

    private ModelSessionManager modelSessionManager;
    private Switchboard switchboard;
    private ConnectorPlug plug;
    private Modifier modifer;

    @Override
    public void init(AnkorSystem system) {
        this.modelSessionManager = system.getModelSessionManager();
        this.switchboard = system.getSwitchboard();
        this.plug = system.getSwitchboard().getConnectorPlug();
        this.modifer = system.getModifier();
    }

    @Override
    public void start() {
        plug.registerTransmissionHandler(LocalParty.class, new LocalTransmissionHandler(modelSessionManager, modifer));
        plug.registerConnectionHandler(LocalParty.class, new LocalConnectionHandler(modelSessionManager, switchboard));
    }

    @Override
    public void stop() {
        plug.unregisterTransmissionHandler(LocalParty.class);
        plug.unregisterConnectionHandler(LocalParty.class);
    }

}
