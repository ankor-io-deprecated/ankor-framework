package at.irian.ankor.fx;

import at.irian.ankor.application.Application;
import at.irian.ankor.connector.local.LocalModelSessionParty;
import at.irian.ankor.connector.socket.SocketParty;
import at.irian.ankor.event.dispatch.JavaFxEventDispatcherFactory;
import at.irian.ankor.fx.binding.fxref.FxRefContext;
import at.irian.ankor.fx.binding.fxref.FxRefContextFactoryProvider;
import at.irian.ankor.fx.binding.fxref.FxRefs;
import at.irian.ankor.msg.ConnectMessage;
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
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketFxClientApplication.class);

    private static final String DEFAULT_MODEL_NAME = "root";
    private static final String DEFAULT_SERVER_ADDRESS = "//localhost:8080";
    private static final String DEFAULT_CLIENT_ADDRESS = "//localhost:9090";

    private String applicationName;
    private String clientAddress;
    private String serverAddress;
    private String modelName = DEFAULT_MODEL_NAME;
    private String applicationInstanceId = null;

    protected SocketFxClientApplication() {
        this("Unnamed FxClient Application");
    }

    protected SocketFxClientApplication(String applicationName) {
        this.applicationName = applicationName;
    }

    protected String getApplicationName() {
        return applicationName;
    }

    protected void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    protected String getClientAddress() {
        return clientAddress;
    }

    protected void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    protected String getServerAddress() {
        return serverAddress;
    }

    protected void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    protected String getModelName() {
        return modelName;
    }

    protected void setModelName(String modelName) {
        this.modelName = modelName;
    }

    protected String getApplicationInstanceId() {
        return applicationInstanceId;
    }

    protected void setApplicationInstanceId(String applicationInstanceId) {
        this.applicationInstanceId = applicationInstanceId;
    }

    @Override
    public void start(Stage stage) throws Exception {
        preCreateAnkorSystem();
        AnkorSystem ankorSystem = createAnkorSystem();
        startAnkorSystem(ankorSystem);
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
    }

    protected AnkorSystem createAnkorSystem() {
        return new AnkorSystemBuilder()
                .withName(applicationName)
                .withConfigValue("at.irian.ankor.connector.socket.SocketConnector.localAddress", getClientAddress())
                .withDispatcherFactory(new JavaFxEventDispatcherFactory())
                .withRefContextFactoryProvider(new FxRefContextFactoryProvider())
                .withRoutingTable(new FixedPairRoutingTable())
                .createClient();
    }

    protected void startAnkorSystem(AnkorSystem ankorSystem) {

        SingletonModelSessionManager modelSessionManager
                = (SingletonModelSessionManager) ankorSystem.getModelSessionManager();
        ModelSession modelSession = modelSessionManager.getModelSession();
        FxRefContext refContext = (FxRefContext) modelSession.getRefContext();
        modelSessionManager.getApplicationInstance().init(refContext);

        Party clientParty = new LocalModelSessionParty(modelSession.getId(), getModelName());
        Party serverParty = new SocketParty(URI.create(getServerAddress()), getModelName());
        ankorSystem.getRoutingTable().connect(clientParty, serverParty);

        // store the singleton RefContext in a static place - for access from FX controllers and FX event handlers
        FxRefs.setStaticRefContext(refContext);

        // Gentlemen, start your engines...
        ankorSystem.start();

        // Send the "connect" message to the server
        Map<String, Object> connectParams;
        if (getApplicationInstanceId() != null) {
            connectParams = new HashMap<>();
            connectParams.put(Application.APPLICATION_INSTANCE_ID_PARAM, getApplicationInstanceId());
        } else {
            connectParams = Collections.emptyMap();
        }
        ankorSystem.getMessageBus().broadcast(new ConnectMessage(clientParty, getModelName(), connectParams));
    }

    public abstract void startFx(Stage stage) throws Exception;

}
