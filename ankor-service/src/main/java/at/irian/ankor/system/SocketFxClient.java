package at.irian.ankor.system;

import at.irian.ankor.event.dispatch.JavaFxEventDispatcherFactory;
import at.irian.ankor.fx.binding.fxref.FxRefContext;
import at.irian.ankor.fx.binding.fxref.FxRefContextFactoryProvider;
import at.irian.ankor.fx.binding.fxref.FxRefs;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.session.SingletonModelSessionManager;
import at.irian.ankor.switching.routing.ClientSocketRoutingLogic;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class SocketFxClient implements AnkorClient {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketFxClient.class);

    private final String applicationName;
    private Map<String, Object> connectParams;
    private final String modelName;
    private final String clientAddress;
    private final String serverAddress;
    private final AnkorSystem ankorSystem;

    SocketFxClient(String applicationName,
                   String modelName, Map<String, Object> connectParams,
                   String clientAddress,
                   String serverAddress,
                   AnkorSystem ankorSystem) {
        this.applicationName = applicationName;
        this.connectParams = connectParams != null ? connectParams : Collections.<String, Object>emptyMap();
        this.modelName = modelName;
        this.clientAddress = clientAddress;
        this.serverAddress = serverAddress;
        this.ankorSystem = ankorSystem;
    }

    public static AnkorClient create(String applicationName,
                                     String modelName,
                                     Map<String, Object> connectParams,
                                     String clientAddress,
                                     String serverAddress) {
        AnkorSystem ankorSystem = createAnkorSystem(applicationName, clientAddress, serverAddress);
        return new SocketFxClient(applicationName,
                                  modelName,
                                  connectParams,
                                  clientAddress,
                                  serverAddress,
                                  ankorSystem);
    }


    private static AnkorSystem createAnkorSystem(String applicationName,
                                                 String clientAddress,
                                                 String serverAddress) {
        LOG.debug("Creating FxClient Ankor system '{}' ...", applicationName);
        AnkorSystem ankorSystem = new AnkorSystemBuilder()
                .withName(applicationName)
                .withConfigValue("at.irian.ankor.switching.connector.socket.SocketConnector.enabled", true)
                .withConfigValue("at.irian.ankor.switching.connector.socket.SocketConnector.localAddress",
                                 clientAddress)
                .withDispatcherFactory(new JavaFxEventDispatcherFactory())
                .withRefContextFactoryProvider(new FxRefContextFactoryProvider())
                .withRoutingLogic(new ClientSocketRoutingLogic(URI.create(serverAddress)))
                .createClient();
        LOG.debug("FxClient Ankor system '{}' created", ankorSystem.getSystemName());
        return ankorSystem;
    }



    @Override
    public void start() {

        LOG.debug("Starting FxClient Ankor system '{}' ...", ankorSystem.getSystemName());

        SingletonModelSessionManager modelSessionManager
                = (SingletonModelSessionManager) ankorSystem.getModelSessionManager();
        ModelSession modelSession = modelSessionManager.getModelSession();
        FxRefContext refContext = (FxRefContext) modelSession.getRefContext();

        // store the singleton RefContext in a static place - for access from FX controllers and FX event handlers
        FxRefs.setStaticRefContext(refContext);

        // Gentlemen, start your engines...
        ankorSystem.start();

        LOG.debug("FxClient Ankor system '{}' was started", applicationName);

        LOG.info("Opening connection from {} to server {} ...", clientAddress, serverAddress);
        // Send the "connect" message to the server
        refContext.openModelConnection(modelName, connectParams);
    }

    @Override
    public void stop() {
        ankorSystem.stop();
    }

}
