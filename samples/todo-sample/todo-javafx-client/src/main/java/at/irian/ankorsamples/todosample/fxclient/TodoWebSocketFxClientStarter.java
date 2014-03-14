package at.irian.ankorsamples.todosample.fxclient;

import at.irian.ankor.system.WebSocketFxClientApplication;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class TodoWebSocketFxClientStarter extends WebSocketFxClientApplication {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TodoWebSocketFxClientStarter.class);

    // used to open the browser
    private static HostServices services;

    public static void main(String[] args) {
        launch(args);
    }

    public TodoWebSocketFxClientStarter() {
        super("Todo FX Client", "root");
    }

    @Override
    public void startFx(Stage stage) throws Exception {
        services = getHostServices();

        stage.setTitle("Ankor JavaFX Todo Sample");
        Pane myPane = FXMLLoader.load(getClass().getClassLoader().getResource("tasks.fxml"));

        Scene myScene = new Scene(myPane);
        myScene.getStylesheets().add("style.css");
        stage.setScene(myScene);
        stage.show();
    }

    public static HostServices getServices() {
        return services;
    }
}