package at.irian.ankor.switching.connector.local;

import at.irian.ankor.application.Application;
import at.irian.ankor.switching.Switchboard;
import at.irian.ankor.switching.connector.Connector;
import at.irian.ankor.switching.party.LocalParty;
import at.irian.ankor.messaging.modify.Modifier;
import at.irian.ankor.session.ModelSessionManager;
import at.irian.ankor.system.AnkorSystem;

/**
 * @author Manfred Geiler
 */
@SuppressWarnings("UnusedDeclaration")  // indirectly called by ServiceLoader
public class LocalConnector implements Connector {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LocalConnector.class);

    private ModelSessionManager modelSessionManager;
    private Application application;
    private Switchboard switchboard;
    private Modifier modifer;

    @Override
    public void init(AnkorSystem system) {
        this.modelSessionManager = system.getModelSessionManager();
        this.application = system.getApplication();
        this.switchboard = system.getSwitchboard();
        this.modifer = system.getModifier();
    }

    @Override
    public void start() {
        switchboard.registerOpenHandler(new ModelSessionOpenHandler(modelSessionManager, application));
        switchboard.registerSendHandler(LocalParty.class, new LocalSendHandler(modelSessionManager, modifer,
                                                                               switchboard));
        switchboard.registerCloseHandler(LocalParty.class, new LocalCloseHandler(modelSessionManager));
    }

    @Override
    public void stop() {
        switchboard.unregisterOpenHandler();
        switchboard.unregisterSendHandler(LocalParty.class);
        switchboard.unregisterCloseHandler(LocalParty.class);
    }

}
