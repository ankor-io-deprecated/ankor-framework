package at.irian.ankor.fx;

import at.irian.ankor.application.Application;
import at.irian.ankor.connector.local.LocalParty;
import at.irian.ankor.connector.socket.SocketParty;
import at.irian.ankor.event.dispatch.JavaFxEventDispatcherFactory;
import at.irian.ankor.fx.binding.fxref.FxRefContext;
import at.irian.ankor.fx.binding.fxref.FxRefContextFactoryProvider;
import at.irian.ankor.fx.binding.fxref.FxRefs;
import at.irian.ankor.msg.ConnectRequestMessage;
import at.irian.ankor.msg.FixedPairRoutingTable;
import at.irian.ankor.msg.party.Party;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.session.SingletonModelSessionManager;
import at.irian.ankor.system.AnkorSystem;
import at.irian.ankor.system.AnkorSystemBuilder;
import javafx.stage.Stage;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public abstract class SocketFxClientApplication extends javafx.application.Application {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketFxClientApplication.class);

   private static final String DEFAULT_SERVER_ADDRESS = "//localhost:8080";
    private static final String DEFAULT_CLIENT_ADDRESS = "//localhost:9090";

    private final String applicationName;
    private final String modelName;
    private String clientAddress;
    private String serverAddress;
    private String appInstanceIdToConnect = null;

    protected SocketFxClientApplication(String applicationName, String modelName) {
        this.applicationName = applicationName;
        this.modelName = modelName;
    }

    protected String getApplicationName() {
        return applicationName;
    }

    protected String getModelName() {
        return modelName;
    }

    protected String getClientAddress() {
        return clientAddress;
    }

    protected void setClientAddress(String clientAddress) {
        LOG.debug("Client address is {}", serverAddress);
        this.clientAddress = clientAddress;
    }

    protected String getServerAddress() {
        return serverAddress;
    }

    protected void setServerAddress(String serverAddress) {
        LOG.debug("Server address is {}", serverAddress);
        this.serverAddress = serverAddress;
    }

    protected String getAppInstanceIdToConnect() {
        return appInstanceIdToConnect;
    }

    /**
     * @param appInstanceIdToConnect  ID of application instance on server to connect to (for collaboration)
     *                                or null if server shall create a new instance on every connect
     */
    @SuppressWarnings("UnusedDeclaration")
    protected void setAppInstanceIdToConnect(String appInstanceIdToConnect) {
        this.appInstanceIdToConnect = appInstanceIdToConnect;
    }

    /**
     * Method that is called back by FX on startup.
     * @param stage Stage
     * @throws Exception
     */
    @Override
    public final void start(Stage stage) throws Exception {
        preCreateAnkorSystem();
        AnkorSystem ankorSystem = createAnkorSystem();
        startAnkorSystemAndConnect(ankorSystem);
        startFx(stage);
    }

    protected void preCreateAnkorSystem() {
        Map<String,String> params = getParameters().getNamed();

        String serverAddress = params.get("server");
        if (serverAddress != null) {
            setServerAddress(serverAddress);
        } else {
            setServerAddress(DEFAULT_SERVER_ADDRESS);
        }

        String clientAddress = params.get("client");
        if (clientAddress != null) {
            setClientAddress(clientAddress);
        } else {
            setClientAddress(DEFAULT_CLIENT_ADDRESS);
        }

        String appInstanceId = params.get("appInstanceId");
        if (appInstanceId != null) {
            setAppInstanceIdToConnect(appInstanceId);
        }
    }

    protected AnkorSystem createAnkorSystem() {
        LOG.debug("Creating FxClient Ankor system '{}' ...", getApplicationName());
        AnkorSystem ankorSystem = new AnkorSystemBuilder()
                .withName(applicationName)
                .withConfigValue("at.irian.ankor.connector.socket.SocketConnector.enabled", true)
                .withConfigValue("at.irian.ankor.connector.socket.SocketConnector.localAddress", getClientAddress())
                .withDispatcherFactory(new JavaFxEventDispatcherFactory())
                .withRefContextFactoryProvider(new FxRefContextFactoryProvider())
                .withRoutingTable(new FixedPairRoutingTable())
                .createClient();
        LOG.debug("FxClient Ankor system '{}' created", getApplicationName());
        return ankorSystem;
    }

    protected void startAnkorSystemAndConnect(AnkorSystem ankorSystem) {

        LOG.debug("Starting FxClient Ankor system '{}' ...", getApplicationName());

        SingletonModelSessionManager modelSessionManager
                = (SingletonModelSessionManager) ankorSystem.getModelSessionManager();
        ModelSession modelSession = modelSessionManager.getModelSession();
        FxRefContext refContext = (FxRefContext) modelSession.getRefContext();
        modelSessionManager.getApplicationInstance().init(refContext);

        Party clientParty = new LocalParty(modelSession.getId(), getModelName());
        Party serverParty = new SocketParty(URI.create(getServerAddress()), getModelName());
        ankorSystem.getRoutingTable().connect(clientParty, serverParty);

        // store the singleton RefContext in a static place - for access from FX controllers and FX event handlers
        FxRefs.setStaticRefContext(refContext);

        // Gentlemen, start your engines...
        ankorSystem.start();

        LOG.debug("FxClient Ankor system '{}' was started", getApplicationName());

        LOG.debug("Sending connect request to server at {} ...", serverAddress);
        // Send the "connect" message to the server
        Map<String, Object> connectParams;
        if (getAppInstanceIdToConnect() != null) {
            connectParams = new HashMap<>();
            connectParams.put(Application.APPLICATION_INSTANCE_ID_PARAM, getAppInstanceIdToConnect());
        } else {
            connectParams = Collections.emptyMap();
        }
        ankorSystem.getMessageBus().broadcast(new ConnectRequestMessage(clientParty, getModelName(), connectParams));
        LOG.debug("Connect request sent to server at {}", serverAddress);
    }

    public abstract void startFx(Stage stage) throws Exception;

}
