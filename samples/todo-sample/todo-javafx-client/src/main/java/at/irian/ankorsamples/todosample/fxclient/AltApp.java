package at.irian.ankorsamples.todosample.fxclient;

import at.irian.ankor.event.dispatch.JavaFxEventDispatcherFactory;
import at.irian.ankor.fx.binding.fxref.FxRefContextFactoryProvider;
import at.irian.ankor.fx.binding.fxref.FxRefFactory;
import at.irian.ankor.messaging.json.viewmodel.ViewModelJsonMessageMapper;
import at.irian.ankor.session.SingletonSessionManager;
import at.irian.ankor.system.AnkorSystem;
import at.irian.ankor.system.AnkorSystemBuilder;
import at.irian.ankor.websocket.WebSocketMessageBus;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.websocket.DeploymentException;
import java.io.IOException;

public class AltApp extends javafx.application.Application {

    private static HostServices services;
    private static FxRefFactory refFactory;

    public static void main(String[] args) {
        launch(args);
    }

    public static FxRefFactory refFactory() {
        return refFactory;
    }

    @Override
    public void start(Stage stage) throws Exception {
        setupAnkor();

        services = getHostServices();

        stage.setTitle("Ankor Todo Sample");
        Pane myPane = FXMLLoader.load(getClass().getClassLoader().getResource("tasks.fxml"));

        Scene myScene = new Scene(myPane);
        myScene.getStylesheets().add("style.css");
        stage.setScene(myScene);
        stage.show();
    }

    private void setupAnkor() throws IOException, DeploymentException, InterruptedException {
        AnkorSystemBuilder appBuilder = new AnkorSystemBuilder();

        appBuilder = appBuilder
                .withMessageBus(new WebSocketMessageBus(new ViewModelJsonMessageMapper(appBuilder.getBeanMetadataProvider())))
                .withRefContextFactoryProvider(new FxRefContextFactoryProvider())
                .withDispatcherFactory(new JavaFxEventDispatcherFactory());

        AnkorSystem app = appBuilder.createWebSocketClient("ws://localhost:8080/websocket/ankor").start();

        // TODO: Hide this from the user
        refFactory = (FxRefFactory) ((SingletonSessionManager) app.getSessionManager()).getSession().getRefContext().refFactory();
    }
}
