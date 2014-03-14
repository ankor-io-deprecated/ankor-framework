package at.irian.ankor.system;

import at.irian.ankor.application.CollaborationSingleRootApplication;
import at.irian.ankor.event.dispatch.JavaFxEventDispatcherFactory;
import at.irian.ankor.fx.binding.fxref.FxRefContext;
import at.irian.ankor.fx.binding.fxref.FxRefContextFactoryProvider;
import at.irian.ankor.fx.binding.fxref.FxRefs;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.session.SingletonModelSessionManager;
import at.irian.ankor.switching.connector.websocket.WebSocketEndpoint;
import at.irian.ankor.switching.connector.websocket.WebSocketModelAddress;
import at.irian.ankor.switching.routing.FixedWebSocketRoutingLogic;
import at.irian.ankor.worker.WorkerContext;
import javafx.stage.Stage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Manfred Geiler
 */
public abstract class WebSocketFxClientApplication extends javafx.application.Application {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(WebSocketFxClientApplication.class);

    private static final String DEFAULT_SERVER_ADDRESS = "localhost:8080";

    private final String applicationName;
    private final String modelName;
    private String serverAddress;
    private String modelInstanceIdToConnect = null;
    private AnkorSystem ankorSystem;
    private final String clientId;
    private final WebSocketConnectionManager connectionManager;
    private final WorkerContext workerContext;

    protected WebSocketFxClientApplication(String applicationName, String modelName) {
        this.applicationName = applicationName;
        this.modelName = modelName;
        this.clientId = UUID.randomUUID().toString();
        this.connectionManager = new WebSocketConnectionManager();
        this.workerContext = new WorkerContext();
    }

    protected String getApplicationName() {
        return applicationName;
    }

    protected String getModelName() {
        return modelName;
    }

    protected String getServerAddress() {
        return serverAddress;
    }

    protected void setServerAddress(String serverAddress) {
        LOG.debug("Server address is {}", serverAddress);
        this.serverAddress = serverAddress;
    }

    protected String getModelInstanceIdToConnect() {
        return modelInstanceIdToConnect;
    }

    /**
     * @param modelInstanceIdToConnect  ID of model instance on server to connect to (for collaboration)
     *                                  or null if server shall create a new instance on every connect
     */
    @SuppressWarnings("UnusedDeclaration")
    protected void setModelInstanceIdToConnect(String modelInstanceIdToConnect) {
        this.modelInstanceIdToConnect = modelInstanceIdToConnect;
    }

    /**
     * Method that is called back by FX on startup.
     * @param stage Stage
     * @throws Exception
     */
    @Override
    public final void start(Stage stage) throws Exception {
        preCreateAnkorSystem();
        createAnkorSystem();
        startAnkorSystemAndConnect();
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

        String appInstanceId = params.get("appInstanceId");
        if (appInstanceId != null) {
            setModelInstanceIdToConnect(appInstanceId);
        }
    }

    protected AnkorSystem createAnkorSystem() {
        LOG.debug("Creating FxClient Ankor system '{}' ...", getApplicationName());
        ankorSystem = new AnkorSystemBuilder()
                .withName(applicationName)
                .withConfigValue("at.irian.ankor.switching.connector.socket.SocketConnector.enabled", false)
                .withConfigValue("at.irian.ankor.switching.connector.websocket.WebSocketConnector.enabled", true)
                .withDispatcherFactory(new JavaFxEventDispatcherFactory())
                .withRefContextFactoryProvider(new FxRefContextFactoryProvider())
                .withOpenHandler(new FixedWebSocketRoutingLogic(clientId))
                .createClient();
        LOG.debug("FxClient Ankor system '{}' created", getApplicationName());
        return ankorSystem;
    }

    protected void startAnkorSystemAndConnect() {

        LOG.debug("Starting FxClient Ankor system '{}' ...", getApplicationName());

        SingletonModelSessionManager modelSessionManager
                = (SingletonModelSessionManager) ankorSystem.getModelSessionManager();
        ModelSession modelSession = modelSessionManager.getModelSession();
        FxRefContext refContext = (FxRefContext) modelSession.getRefContext();
        // store the singleton RefContext in a static place - for access from FX controllers and FX event handlers
        FxRefs.setStaticRefContext(refContext);
        // Set WorkerContext
        WorkerContext.setCurrentInstance(workerContext);
        // Gentlemen, start your engines...
        ankorSystem.start();
        // Connect to WebSocket
        connectionManager.connect();

        LOG.debug("FxClient Ankor system '{}' was started", getApplicationName());

    }

    protected int getHeartbeatIntervalSeconds() {
        return 25;
    }

    protected int getReconnectIntervalMillis() {
        return 500;
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        ankorSystem.stop();
    }

    public abstract void startFx(Stage stage) throws Exception;

    // -------------------------------- CONNECTION MANAGER --------------------------------

    private class WebSocketConnectionManager {

        private final AtomicBoolean connected = new AtomicBoolean(false);
        private CountDownLatch latch;
        private Timer heartBeatTimer;

        private WebSocketConnectionManager() {
        }

        public void notifyOpen(Session session) {
            latch.countDown();
            connected.set(true);
            startHeartbeat(session);
        }

        public void notifyError() {
            latch.countDown();
            connected.set(false);
            stopHeartbeat();
            closeModelConnection();
            reConnect();
        }

        public void notifyClose() {
            latch.countDown();
            connected.set(false);
            stopHeartbeat();
            closeModelConnection();
            reConnect();
        }

        public void connect() {
            internalConnect();
            if (!connected.get()) {
                reConnect();
            }
        }

        public void reConnect() {
            if (!connected.get()) {
                final Timer reconnectTimer = new Timer();
                reconnectTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!connected.get()) {
                            try {
                                WorkerContext.setCurrentInstance(workerContext);
                                internalConnect();
                            } finally {
                                if (connected.get()) {
                                    reconnectTimer.cancel();
                                }
                                WorkerContext.setCurrentInstance(null);
                            }
                        } else {
                            reconnectTimer.cancel();
                        }
                    }
                }, getReconnectIntervalMillis(), getReconnectIntervalMillis());
            }
        }

        private void stopHeartbeat() {
            if (heartBeatTimer != null) {
                heartBeatTimer.cancel();
                heartBeatTimer = null;
            }
        }

        private void startHeartbeat(final Session session) {
            if (heartBeatTimer != null) {
                throw new IllegalStateException("Heartbeat Timer already running");
            }
            heartBeatTimer = new Timer();
            heartBeatTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (connected.get()) {
                        session.getAsyncRemote().sendText("");
                    }
                }
            }, getHeartbeatIntervalSeconds() * 1000, getHeartbeatIntervalSeconds() * 1000);
        }

        private void internalConnect() {
            latch = new CountDownLatch(1);
            LOG.info("Opening connection to server {} ...", getServerAddress());
            String uri = "ws://" + getServerAddress() + "/websocket/ankor/" + clientId;
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            //noinspection TryWithIdenticalCatches
            try {
                container.connectToServer(new FxEndpoint(), URI.create(uri));
            } catch (DeploymentException e) {
                LOG.error("DeploymentException on connectToServer " + e.getMessage());
                latch.countDown();
            } catch (IOException e) {
                LOG.error("IOException on connectToServer " + e.getMessage());
                latch.countDown();
            }

            try {
                if (!latch.await(10, TimeUnit.SECONDS)) {
                    LOG.debug("WebSocket connect timeout");
                }
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
            if (connected.get()) {
                openModelConnection();
            }
        }

        private void closeModelConnection() {
            ankorSystem.getSwitchboard().closeAllConnections(new WebSocketModelAddress(clientId, getModelName()));
        }

        private void openModelConnection() {
            // Send the "connect" message to the server
            Map<String, Object> connectParams;
            if (getModelInstanceIdToConnect() != null) {
                //noinspection unchecked
                connectParams = new HashMap();
                connectParams.put(CollaborationSingleRootApplication.MODEL_INSTANCE_ID_PARAM,
                        getModelInstanceIdToConnect());
            } else {
                connectParams = Collections.emptyMap();
            }
            FxRefs.refContext().openModelConnection(getModelName(), connectParams);
        }
    }

    // -------------------------------- ENDPOINT --------------------------------

    private class FxEndpoint extends WebSocketEndpoint {

        @Override
        protected AnkorSystem getAnkorSystem() {
            return ankorSystem;
        }

        @Override
        public void onOpen(Session session, EndpointConfig config) {
            LOG.info("WebSocket connection established");
            setClientId(session, clientId);
            connectionManager.notifyOpen(session);
            super.onOpen(session, config);
        }

        @Override
        public void onClose(Session session, CloseReason closeReason) {
            LOG.info("WebSocket connection closed code: {} reason: {}", closeReason.getCloseCode(), closeReason.getReasonPhrase());
            connectionManager.notifyClose();
            super.onClose(session, closeReason);
        }

        @Override
        public void onError(Session session, Throwable thr) {
            LOG.info("WebSocket connection closed msg: {} ", thr.getMessage());
            connectionManager.notifyError();
            super.onError(session, thr);
        }

    }

}
