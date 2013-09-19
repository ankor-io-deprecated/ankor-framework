package at.irian.ankorsamples.todosample.fxclient;

import at.irian.ankor.fx.controller.FXControllerChangeListener;
import at.irian.ankor.messaging.json.viewmodel.ViewModelJsonMessageMapper;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.servlet.websocket.messaging.WebSocketMessageBus;
import at.irian.ankor.servlet.websocket.session.WebSocketRemoteSystem;
import at.irian.ankor.session.SingletonSessionManager;
import at.irian.ankor.system.AnkorSystem;
import at.irian.ankor.system.AnkorSystemBuilder;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;

/**
 * @author Thomas Spiegl
 */
public class App extends Application {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(App.class);

    private static RefFactory refFactory;
    private static HostServices services;
    private boolean connected = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        services = getHostServices();
        createWebSocketClientSystem(primaryStage);
        //startFXClient(primaryStage);
    }

    private void startFXClient(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Ankor FX Sample");
        Pane myPane = FXMLLoader.load(getClass().getClassLoader().getResource("tasks.fxml"));

        Scene myScene = new Scene(myPane);
        myScene.getStylesheets().add("style.css");
        primaryStage.setScene(myScene);
        primaryStage.show();
    }

    private void createWebSocketClientSystem(final Stage primaryStage) throws IOException, DeploymentException {
        final String clientId = UUID.randomUUID().toString();

        final AnkorSystem[] clientSystem = new AnkorSystem[1];
        final WebSocketMessageBus messageBus = new WebSocketMessageBus(new ViewModelJsonMessageMapper());
        final AnkorSystemBuilder systemBuilder = new AnkorSystemBuilder()
                .withName(clientId)
                .withGlobalEventListener(new FXControllerChangeListener())
                .withMessageBus(messageBus)
                .withModelContextId("collabTest");

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        String uri = "ws://localhost:8080" + "/websocket/ankor/" + clientId;
        container.connectToServer(new Endpoint() {

            @Override
            public void onOpen(Session session, EndpointConfig config) {
                session.addMessageHandler(new MessageHandler.Whole<String>() {

                    @Override
                    public void onMessage(String message) {
                        messageBus.receiveSerializedMessage(message);
                    }
                });

                messageBus.addRemoteSystem(new WebSocketRemoteSystem(clientId, session));
                final AnkorSystem clientSystem = systemBuilder.createClient();
                RefContext clientRefContext = ((SingletonSessionManager) clientSystem.getSessionManager()).getSession().getRefContext();
                refFactory = clientRefContext.refFactory();
                try {
                    startFXClient(primaryStage);
                } catch (IOException ignored) {
                }
            }
        }, URI.create(uri));
    }

    public static HostServices getServices() {
        return services;
    }

    public static RefFactory refFactory() {
        return refFactory;
    }
}