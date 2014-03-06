package at.irian.ankor.gateway.connector.local;

import at.irian.ankor.application.Application;
import at.irian.ankor.gateway.connector.Connector;
import at.irian.ankor.gateway.party.LocalParty;
import at.irian.ankor.messaging.modify.Modifier;
import at.irian.ankor.gateway.Gateway;
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
    private Gateway gateway;
    private Modifier modifer;

    @Override
    public void init(AnkorSystem system) {
        this.modelSessionManager = system.getModelSessionManager();
        this.application = system.getApplication();
        this.gateway = system.getGateway();
        this.modifer = system.getModifier();
    }

    @Override
    public void start() {
        gateway.registerConnectHandler(new LocalConnectHandler(modelSessionManager, application));
        gateway.registerMessageDeliverer(LocalParty.class, new LocalDeliverHandler(modelSessionManager, modifer,
                                                                                   gateway));
        gateway.registerDisconnectHandler(LocalParty.class, new LocalCloseHandler(modelSessionManager));
    }

    @Override
    public void stop() {
        gateway.unregisterConnectHandler();
        gateway.unregisterMessageDeliverer(LocalParty.class);
        gateway.unregisterDisconnectHandler(LocalParty.class);
    }

}
