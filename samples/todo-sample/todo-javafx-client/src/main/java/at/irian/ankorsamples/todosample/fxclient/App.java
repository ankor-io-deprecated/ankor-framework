package at.irian.ankorsamples.todosample.fxclient;

import at.irian.ankor.fx.websocket.AnkorApplication;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.UUID;

public class App extends AnkorApplication {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(App.class);

    // used to open the browser
    private static HostServices services;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected String getWebSocketUri() {
        return "wss://ankor-todo-sample.irian.at/websocket/ankor";
        // return "ws://localhost:8080/websocket/ankor";
    }

    @Override
    protected void startFXClient(Stage primaryStage) throws Exception {
        services = getHostServices();

        primaryStage.setTitle("Ankor FX Sample");
        Pane myPane = FXMLLoader.load(getClass().getClassLoader().getResource("tasks.fxml"));

        Scene myScene = new Scene(myPane);
        myScene.getStylesheets().add("style.css");
        primaryStage.setScene(myScene);
        primaryStage.show();
    }

    public static HostServices getServices() {
        return services;
    }
}