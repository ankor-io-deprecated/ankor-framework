package at.irian.ankorsamples.todosample.fxclient;

import at.irian.ankor.fx.controller.FXControllerChangeListener;
import at.irian.ankor.messaging.json.viewmodel.ViewModelJsonMessageMapper;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.session.SingletonSessionManager;
import at.irian.ankor.system.AnkorSystem;
import at.irian.ankor.system.AnkorSystemBuilder;
import com.google.gson.JsonElement;
import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;

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
        createSocketIOClientSystem();
        startFXClient(primaryStage);
    }

    private void startFXClient(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Ankor FX Sample");
        Pane myPane = FXMLLoader.load(getClass().getClassLoader().getResource("tasks.fxml"));

        Scene myScene = new Scene(myPane);
        myScene.getStylesheets().add("style.css");
        primaryStage.setScene(myScene);
        primaryStage.show();
    }

    private void createSocketIOClientSystem() {
        try {
            final SocketIO socket = new SocketIO(new URL("http://127.0.0.1:9092"));
            final SocketIOMessageBus messageBus = new SocketIOMessageBus(new ViewModelJsonMessageMapper());

            socket.connect(new IOCallback() {
                @Override
                public void onMessage(String data, IOAcknowledge ack) {
                    messageBus.receiveSerializedMessage(data);
                }

                @Override
                public void onMessage(JsonElement jsonElement, IOAcknowledge ioAcknowledge) {
                }

                @Override
                public void on(String s, IOAcknowledge ioAcknowledge, JsonElement... jsonElements) {
                }

                @Override
                public void onError(SocketIOException socketIOException) {
                }

                @Override
                public void onDisconnect() {
                }

                @Override
                public void onConnect() {
                    try {
                        if (!connected) {
                            connected = true;

                            // XXX: The sessionId is not exposed by the api
                            Field f = socket.getClass().getDeclaredField("connection");
                            f.setAccessible(true);
                            Object connection = (Object) f.get(socket); //IllegalAccessException

                            Field f2 = connection.getClass().getDeclaredField("sessionId"); //NoSuchFieldException
                            f2.setAccessible(true);
                            String sessionId = (String) f2.get(connection); //IllegalAccessException

                            messageBus.addRemoteSystem(new SocketIORemoteSystem(sessionId, socket));
                            AnkorSystem clientSystem = new AnkorSystemBuilder()
                                    .withName(sessionId)
                                    .withGlobalEventListener(new FXControllerChangeListener())
                                    .withMessageBus(messageBus)
                                    .withModelContextId("collabTest")
                                    .createClient();
                            clientSystem.start();

                            RefContext clientRefContext = ((SingletonSessionManager) clientSystem.getSessionManager()).getSession().getRefContext();
                            refFactory = clientRefContext.refFactory();
                        }
                    } catch (NoSuchFieldException | IllegalAccessException ignored) {
                    }
                }
            });
        } catch (MalformedURLException ignored) {
        }
    }

    public static HostServices getServices() {
        return services;
    }

    public static RefFactory refFactory() {
        return refFactory;
    }
}