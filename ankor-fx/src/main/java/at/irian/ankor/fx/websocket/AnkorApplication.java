package at.irian.ankor.fx.websocket;

import at.irian.ankor.event.dispatch.JavaFxEventDispatcherFactory;
import at.irian.ankor.fx.binding.fxref.FxRefContextFactoryProvider;
import at.irian.ankor.fx.binding.fxref.FxRefFactory;
import at.irian.ankor.messaging.json.viewmodel.ViewModelJsonMessageMapper;
import at.irian.ankor.connection.SingletonModelConnectionManager;
import at.irian.ankor.system.AnkorSystem;
import at.irian.ankor.system.AnkorSystemBuilder;
import at.irian.ankor.websocket.AnkorClientEndpoint;
import at.irian.ankor.websocket.WebSocketMessageBus;
import javafx.application.Application;
import javafx.stage.Stage;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Base class for Ankor Java FX applications, that use WebSockets as transport layer.
 *
 * @author Florian Klampfer
 */
public abstract class AnkorApplication extends Application {
    public static final int CONNECT_TIMEOUT = 10000;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorApplication.class);
    private static FxRefFactory refFactory;

    public static void main(String[] args) {
        launch(args);
    }

    public static FxRefFactory refFactory() {
        return refFactory;
    }

    @Override
    public final void start(Stage primaryStage) throws Exception {
        AnkorSystem clientSystem = createWebSocketClientSystem(getWebSocketUri());
        refFactory = (FxRefFactory) ((SingletonModelConnectionManager) clientSystem.getModelConnectionManager())
                .getModelConnection().getRefContext().refFactory();

        startFXClient(primaryStage);
    }

    /**
     * @return The absolute URI of the web socket endpoint.
     */
    protected abstract String getWebSocketUri();

    /**
     * You can use this to make two clients share the state of an app.
     *
     * @return An optional id for the model session.
     */
    protected String getModelSessionId() {
        return null;
    }

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
     * @return A running AnkorSystem
     * @throws IOException when there's an io error while connecting.
     */
    private AnkorSystem createWebSocketClientSystem(String uri) throws IOException, DeploymentException, InterruptedException {
        AnkorSystem clientSystem;
        AnkorSystemBuilder systemBuilder = new AnkorSystemBuilder();
        ViewModelJsonMessageMapper messageMapper
                = new ViewModelJsonMessageMapper(systemBuilder.getBeanMetadataProvider());
        WebSocketMessageBus messageBus = new WebSocketMessageBus(messageMapper);
        systemBuilder = systemBuilder
                .withMessageBus(messageBus)
                .withRefContextFactoryProvider(new FxRefContextFactoryProvider())
                .withDispatcherFactory(new JavaFxEventDispatcherFactory());

        if (getModelSessionId() != null) {
            systemBuilder.withModelSessionId(getModelSessionId());
        }

        CountDownLatch latch = new CountDownLatch(2);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(new AnkorClientEndpoint(systemBuilder, messageBus, latch), URI.create(uri));

        if (latch.await(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)) {
            (clientSystem = systemBuilder.createClient()).start();
        } else {
            LOG.error("Could not connect to {} within {} milliseconds", uri, CONNECT_TIMEOUT);
            throw new IOException("Could not connect within time");
        }

        return clientSystem;
    }
}
