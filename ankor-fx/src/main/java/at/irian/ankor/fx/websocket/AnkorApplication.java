package at.irian.ankor.fx.websocket;

import at.irian.ankor.event.dispatch.JavaFxEventDispatcherFactory;
import at.irian.ankor.fx.binding.fxref.FxRefContextFactoryProvider;
import at.irian.ankor.fx.binding.fxref.FxRefFactory;
import at.irian.ankor.messaging.json.viewmodel.ViewModelJsonMessageMapper;
import at.irian.ankor.servlet.websocket.messaging.WebSocketMessageBus;
import at.irian.ankor.servlet.websocket.session.WebSocketRemoteSystem;
import at.irian.ankor.session.SingletonSessionManager;
import at.irian.ankor.system.AnkorSystem;
import at.irian.ankor.system.AnkorSystemBuilder;
import javafx.application.Application;
import javafx.stage.Stage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Base class for Ankor Java FX applications, that use WebSockets as transport layer.
 *
 * @author Florian Klampfer
 */
public abstract class AnkorApplication extends Application {
    public static final int HEARTBEAT_INTERVAL = 5000;
    public static final int CONNECT_TIMEOUT = 10000;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorApplication.class);
    private static FxRefFactory refFactory;
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) {
        launch(args);
    }

    public static FxRefFactory refFactory() {
        return refFactory;
    }

    @Override
    public final void start(Stage primaryStage) throws Exception {
        AnkorSystem clientSystem = createWebSocketClientSystem(getWebSocketUri());
        refFactory = (FxRefFactory) ((SingletonSessionManager) clientSystem.getSessionManager())
                .getSession().getRefContext().refFactory();

        startFXClient(primaryStage);
    }

    /**
     * @return The absolute URI of the web socket endpoint.
     */
    protected abstract String getWebSocketUri();

    /**
     * Use this method to start the JavaFX application after the web socket connection has been established.
     * This is basically the new {@link #start} method.
     *
     * @param primaryStage the primary {@link Stage} of the FX application.
     * @throws Exception when an error occurs during startup.
     */
    protected abstract void startFXClient(Stage primaryStage) throws Exception;

    /**
     * Connects to the web socket endpoint at {@link #getWebSocketUri()} and starts the {@link AnkorSystem}.
     * This methods blocks until the connection has been established or the time limit is reached.
     *
     * @param uri Uri used for the web socket connection.
     * @return
     * @throws IOException         when there's an io error while connecting.
     * @throws DeploymentException when
     */
    private AnkorSystem createWebSocketClientSystem(String uri) throws IOException {

        final String clientId = UUID.randomUUID().toString();

        final CountDownLatch latch = new CountDownLatch(2);

        final AnkorSystem[] clientSystem = new AnkorSystem[1];
        final WebSocketMessageBus messageBus = new WebSocketMessageBus(new ViewModelJsonMessageMapper());
        final AnkorSystemBuilder systemBuilder = new AnkorSystemBuilder()
                .withName(clientId)
                .withMessageBus(messageBus)
                .withRefContextFactoryProvider(new FxRefContextFactoryProvider())
                .withDispatcherFactory(new JavaFxEventDispatcherFactory());

        if (getModelContextId() != null) {
            systemBuilder.withModelContextId(getModelContextId());
        }

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        try {
            container.connectToServer(new Endpoint() {

                @Override
                public void onOpen(final Session session, EndpointConfig config) {

                    session.addMessageHandler(new MessageHandler.Whole<String>() {
                        private boolean idReceived = false;

                        @Override
                        public void onMessage(String message) {
                            if (!idReceived) {
                                systemBuilder.withName(message);
                                messageBus.addRemoteSystem(new WebSocketRemoteSystem(message, session));
                                latch.countDown();

                                idReceived = true;
                            } else {
                                messageBus.receiveSerializedMessage(message);
                            }
                        }
                    });

                    startHeartbeat(session);
                    latch.countDown();
                }

                /**
                 * Schedules a heartbeat message every fixed milliseconds.
                 *
                 * @param session The web socket {@link Session} used for sending the heartbeat messages.
                 */
                private void startHeartbeat(final Session session) {
                    LOG.info("Starting heartbeat");

                    scheduler.scheduleAtFixedRate(new Runnable() {
                        @Override
                        public void run() {
                            session.getAsyncRemote().sendText(""); // heartbeat
                        }
                    }, HEARTBEAT_INTERVAL, HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);
                }

            }, URI.create(uri));

            if (latch.await(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)) {
                (clientSystem[0] = systemBuilder.createClient()).start();
            } else {
                LOG.error("Could not connect to {} within {} milliseconds", uri, CONNECT_TIMEOUT);
                throw new IOException("Could not connect within time");
            }

        } catch (DeploymentException | InterruptedException ignored) {
        }

        return clientSystem[0];
    }

    protected String getModelContextId() {
        return null;
    }
}
