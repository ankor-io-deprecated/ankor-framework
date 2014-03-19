package at.irian.ankor.system;

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

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Thomas Spiegl
 */
public class WebSocketClient {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(WebSocketClient.class);

    private final String applicationName;
    private final String modelName;
    private final Map<String, Object> connectParams;
    private final String host;
    private final int port;
    private final String serverPath;
    private final long heartbeatIntervalMillis;
    private final long reconnectIntervalMillis;
    private final Endpoint listener;

    private AnkorSystem ankorSystem;
    private final String clientId;
    private final WebSocketConnectionManager connectionManager;
    private final WorkerContext workerContext;

    public WebSocketClient(String applicationName, String modelName, Map<String, Object> connectParams, String host, int port, String serverPath, Endpoint listener) {
        this.listener = listener;
        if (applicationName == null) { throw new NullPointerException("applicationName"); }
        if (modelName == null) { throw new NullPointerException("modelName"); }
        if (connectParams == null) { throw new NullPointerException("connectParams"); }
        if (host == null) { throw new NullPointerException("host"); }
        if (serverPath == null) { throw new NullPointerException("serverPath"); }
        this.applicationName = applicationName;
        this.modelName = modelName;
        this.connectParams = connectParams;
        this.host = host;
        this.port = port;
        if (serverPath.startsWith("/")) {
            serverPath = serverPath.substring(1);
        }
        if (serverPath.endsWith("/")) {
            serverPath = serverPath.substring(0, serverPath.length() - 1);
        }
        this.serverPath = serverPath;
        this.clientId = UUID.randomUUID().toString();
        this.connectionManager = new WebSocketConnectionManager();
        this.workerContext = new WorkerContext();
        this.heartbeatIntervalMillis = 25 * 1000;
        this.reconnectIntervalMillis = 500;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String applicationName;
        private String modelName;
        private Map<String, Object> connectParams = new HashMap<>();
        private String host;
        private int port;
        private String path;
        private Endpoint listener;

        public Builder withApplicationName(String applicationName) {
            this.applicationName = applicationName;
            return this;
        }

        public Builder withModelName(String modelName) {
            this.modelName = modelName;
            return this;
        }

        public Builder withConnectParam(String key, Object value) {
            connectParams.put(key, value);
            return this;
        }

        public Builder withServer(String host, int port, String path) {
            this.host = host;
            this.port = port;
            this.path = path;
            return this;
        }

        public Builder withEndpointListener(Endpoint listener) {
            this.listener = listener;
            return this;
        }

        public WebSocketClient build() {
            return new WebSocketClient(applicationName, modelName, connectParams, host, port, path, listener);
        }
    }

    public final void start() throws Exception {
        createAnkorSystem();
        startAnkorSystemAndConnect();
    }

    private AnkorSystem createAnkorSystem() {
        LOG.debug("Creating FxClient Ankor system '{}' ...", applicationName);
        ankorSystem = new AnkorSystemBuilder()
                .withName(applicationName)
                .withConfigValue("at.irian.ankor.switching.connector.socket.SocketConnector.enabled", false)
                .withConfigValue("at.irian.ankor.switching.connector.websocket.WebSocketConnector.enabled", true)
                .withDispatcherFactory(new JavaFxEventDispatcherFactory(workerContext))
                .withRefContextFactoryProvider(new FxRefContextFactoryProvider())
                .withOpenHandler(new FixedWebSocketRoutingLogic(clientId))
                .createClient();
        LOG.debug("FxClient Ankor system '{}' created", applicationName);
        return ankorSystem;
    }

    private void startAnkorSystemAndConnect() {

        LOG.debug("Starting FxClient Ankor system '{}' ...", applicationName);

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

        LOG.debug("FxClient Ankor system '{}' was started", applicationName);

    }

    public void stop() throws Exception {
        ankorSystem.stop();
    }

    // -------------------------------- CONNECTION MANAGER --------------------------------

    private class WebSocketConnectionManager {

        private final AtomicBoolean connected = new AtomicBoolean(false);
        private CountDownLatch latch;
        private Timer heartBeatTimer;
        private boolean exceptionOnConnect;

        private WebSocketConnectionManager() {
        }

        public void notifyOpen(Session session) {
            latch.countDown();
            connected.set(true);
            exceptionOnConnect = false;
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
            if (exceptionOnConnect) {
                LOG.warn("No reconnect due to previous connection error");
                return;
            }
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
                }, reconnectIntervalMillis, reconnectIntervalMillis);
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
            }, heartbeatIntervalMillis, heartbeatIntervalMillis);
        }

        private void internalConnect() {
            latch = new CountDownLatch(1);
            String uri = String.format("ws://%s:%d/%s/%s", host, port, serverPath, clientId);
            LOG.info("Connecting to " + uri);
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            //noinspection TryWithIdenticalCatches
            try {
                container.connectToServer(new FxEndpoint(), URI.create(uri));
            } catch (DeploymentException e) {
                exceptionOnConnect = true;
                LOG.error(String.format("DeploymentException on connectToServer %s %s", e.getMessage(), uri));
                latch.countDown();
            } catch (IOException e) {
                exceptionOnConnect = true;
                LOG.error(String.format("IOException on connectToServer %s %s", e.getMessage(), uri));
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
            ankorSystem.getSwitchboard().closeAllConnections(new WebSocketModelAddress(clientId, modelName));
        }

        private void openModelConnection() {
            // Send the "connect" message to the server
            FxRefs.refContext().openModelConnection(modelName, connectParams);
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
            if (listener != null) {
                listener.onOpen(session, config);
            }
            super.onOpen(session, config);
        }

        @Override
        public void onClose(Session session, CloseReason closeReason) {
            LOG.info("WebSocket connection closed code: {} reason: {}", closeReason.getCloseCode(), closeReason.getReasonPhrase());
            connectionManager.notifyClose();
            if (listener != null) {
                listener.onClose(session, closeReason);
            }
            super.onClose(session, closeReason);
        }

        @Override
        public void onError(Session session, Throwable thr) {
            LOG.info("WebSocket connection closed msg: {} ", thr.getMessage());
            connectionManager.notifyError();
            if (listener != null) {
                listener.onError(session, thr);
            }
            super.onError(session, thr);
        }

    }

}
