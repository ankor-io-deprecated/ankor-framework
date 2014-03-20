package at.irian.ankor.system;

import at.irian.ankor.application.CollaborationSingleRootApplication;
import at.irian.ankor.event.dispatch.JavaFxEventDispatcherFactory;
import at.irian.ankor.fx.binding.fxref.FxRefContext;
import at.irian.ankor.fx.binding.fxref.FxRefContextFactoryProvider;
import at.irian.ankor.fx.binding.fxref.FxRefs;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.session.SingletonModelSessionManager;
import at.irian.ankor.switching.routing.FixedSocketRoutingLogic;
import javafx.application.Application;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class SocketFxClient implements AnkorClient {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketFxClient.class);

    private static final String DEFAULT_SERVER_ADDRESS = "//localhost:8080";
    private static final String DEFAULT_CLIENT_ADDRESS = "//localhost:9090";

    private final String applicationName;
    private final String applicationInstanceId;
    private final String modelName;
    private final String clientAddress;
    private final String serverAddress;
    private final AnkorSystem ankorSystem;

    public SocketFxClient(String applicationName,
                          String applicationInstanceId,
                          String modelName,
                          String clientAddress,
                          String serverAddress,
                          AnkorSystem ankorSystem) {
        this.applicationName = applicationName;
        this.applicationInstanceId = applicationInstanceId;
        this.modelName = modelName;
        this.clientAddress = clientAddress;
        this.serverAddress = serverAddress;
        this.ankorSystem = ankorSystem;
    }


    public static AnkorClient create(String applicationName,
                                     String modelName) {
        return create(applicationName, modelName, null, DEFAULT_CLIENT_ADDRESS, DEFAULT_SERVER_ADDRESS);
    }

    public static AnkorClient create(String applicationName,
                                     String modelName,
                                     Application.Parameters parameters) {
        Map<String,String> params = parameters.getNamed();

        String serverAddress = params.get("server");
        if (serverAddress == null) {
            serverAddress = DEFAULT_SERVER_ADDRESS;
        }

        String clientAddress = params.get("client");
        if (clientAddress == null) {
            clientAddress = DEFAULT_CLIENT_ADDRESS;
        }

        String appInstanceId = params.get("appInstanceId");

        return create(applicationName, modelName, appInstanceId, clientAddress, serverAddress);
    }

    public static AnkorClient create(String applicationName,
                                     String modelName,
                                     String applicationInstanceId,
                                     String clientAddress) {
        return create(applicationName, modelName, applicationInstanceId, clientAddress, DEFAULT_SERVER_ADDRESS);
    }

    public static AnkorClient create(String applicationName,
                                     String modelName,
                                     String applicationInstanceId,
                                     String clientAddress,
                                     String serverAddress) {
        AnkorSystem ankorSystem = createAnkorSystem(applicationName, clientAddress, serverAddress);
        return new SocketFxClient(applicationName,
                                  applicationInstanceId,
                                  modelName,
                                  clientAddress,
                                  serverAddress,
                                  ankorSystem);
    }


    private static AnkorSystem createAnkorSystem(String applicationName, String clientAddress, String serverAddress) {
        LOG.debug("Creating FxClient Ankor system '{}' ...", applicationName);
        AnkorSystem ankorSystem = new AnkorSystemBuilder()
                .withName(applicationName)
                .withConfigValue("at.irian.ankor.switching.connector.socket.SocketConnector.enabled", true)
                .withConfigValue("at.irian.ankor.switching.connector.socket.SocketConnector.localAddress", clientAddress)
                .withDispatcherFactory(new JavaFxEventDispatcherFactory())
                .withRefContextFactoryProvider(new FxRefContextFactoryProvider())
                .withOpenHandler(new FixedSocketRoutingLogic(URI.create(serverAddress)))
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
        Map<String, Object> connectParams;
        if (applicationInstanceId != null) {
            connectParams = new HashMap<>();
            connectParams.put(CollaborationSingleRootApplication.MODEL_INSTANCE_ID_PARAM,
                              applicationInstanceId);
        } else {
            connectParams = Collections.emptyMap();
        }

        refContext.openModelConnection(modelName, connectParams);
    }

    @Override
    public void stop() {
        ankorSystem.stop();
    }

}
