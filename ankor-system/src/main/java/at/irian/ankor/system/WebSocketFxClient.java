/*
 * Copyright (C) 2013-2014  Irian Solutions  (http://www.irian.at)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.irian.ankor.system;

import at.irian.ankor.event.dispatch.JavaFxEventDispatcherFactory;
import at.irian.ankor.fx.binding.fxref.FxRefContext;
import at.irian.ankor.fx.binding.fxref.FxRefContextFactoryProvider;
import at.irian.ankor.fx.binding.fxref.FxRefs;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.session.SingletonModelSessionManager;
import at.irian.ankor.switching.connector.websocket.WebSocketEndpoint;
import at.irian.ankor.switching.connector.websocket.WebSocketModelAddress;
import at.irian.ankor.switching.routing.ClientWebSocketRoutingLogic;

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
public class WebSocketFxClient implements AnkorClient {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(WebSocketFxClient.class);

    private final String applicationName;
    private final String modelName;
    private final Map<String, Object> connectParams;
    private final String serverPath;
    private final long heartbeatIntervalMillis;
    private final long reconnectIntervalMillis;
    private final WebSocketEndpointListener endpointListener;

    private AnkorSystem ankorSystem;
    private final String clientId;
    private final WebSocketConnectionManager connectionManager;

    public WebSocketFxClient(String applicationName,
                             String modelName,
                             Map<String, Object> connectParams,
                             String serverPath,
                             WebSocketEndpointListener endpointListener) {
        if (applicationName == null) { throw new NullPointerException("applicationName"); }
        if (modelName == null) { throw new NullPointerException("modelName"); }
        if (connectParams == null) { throw new NullPointerException("connectParams"); }
        if (serverPath == null) { throw new NullPointerException("serverPath"); }
        this.applicationName = applicationName;
        this.modelName = modelName;
        this.connectParams = connectParams;
        if (serverPath.endsWith("/")) {
            serverPath = serverPath.substring(0, serverPath.length() - 1);
        }
        this.serverPath = serverPath;
        this.endpointListener = endpointListener;
        this.clientId = UUID.randomUUID().toString();
        this.connectionManager = new WebSocketConnectionManager();
        this.heartbeatIntervalMillis = 25 * 1000;     // todo  config
        this.reconnectIntervalMillis = 500;
    }

    public static WebSocketFxClientBuilder builder() {
        return new WebSocketFxClientBuilder();
    }

    @Override
    public final void start() {
        createAnkorSystem();
        startAnkorSystemAndConnect();
    }

    private AnkorSystem createAnkorSystem() {
        LOG.debug("Creating FxClient Ankor system '{}' ...", applicationName);
        ankorSystem = new AnkorSystemBuilder()
                .withName(applicationName)
                .withConfigValue("at.irian.ankor.switching.connector.socket.SocketConnector.enabled", false)
                .withConfigValue("at.irian.ankor.switching.connector.websocket.WebSocketConnector.enabled", true)
                .withDispatcherFactory(new JavaFxEventDispatcherFactory())
                .withRefContextFactoryProvider(new FxRefContextFactoryProvider())
                .withRoutingLogic(new ClientWebSocketRoutingLogic(clientId))
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
        // Gentlemen, start your engines...
        ankorSystem.start();
        // Connect to WebSocket
        connectionManager.connect();

        LOG.debug("FxClient Ankor system '{}' was started", applicationName);

    }

    @Override
    public void stop() {
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
                                internalConnect();
                            } finally {
                                if (connected.get()) {
                                    reconnectTimer.cancel();
                                }
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
            String uri = String.format("%s/%s", serverPath, clientId);
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
        public String getPath() {
            return "WebSocket";
        }

        @Override
        public void onOpen(Session session, EndpointConfig config) {
            LOG.info("WebSocket connection established");
            setClientId(session, clientId);
            connectionManager.notifyOpen(session);
            if (endpointListener != null) {
                endpointListener.onOpen(session, config);
            }
            super.onOpen(session, config);
        }

        @Override
        public void onClose(Session session, CloseReason closeReason) {
            LOG.info("WebSocket connection closed code: {} reason: {}", closeReason.getCloseCode(), closeReason.getReasonPhrase());
            connectionManager.notifyClose();
            if (endpointListener != null) {
                endpointListener.onClose(session, closeReason);
            }
            super.onClose(session, closeReason);
        }

        @Override
        public void onError(Session session, Throwable thr) {
            LOG.info("WebSocket connection closed msg: {} ", thr.getMessage());
            connectionManager.notifyError();
            if (endpointListener != null) {
                endpointListener.onError(session, thr);
            }
            super.onError(session, thr);
        }

    }

}
