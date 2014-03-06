package at.irian.ankorsamples.todosample.fxclient;

import at.irian.ankor.fx.SocketFxClientApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * @author Manfred Geiler
 */
public class TodoSocketFxClientStarter extends SocketFxClientApplication {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TodoSocketFxClientStarter.class);

    private static final String APPLICATION_NAME = "Todo Sample FX Client";
    private static final String MODEL_NAME = "root";

    public static void main(String[] args) {
        launch(args);
    }

    public TodoSocketFxClientStarter() {
        super(APPLICATION_NAME, MODEL_NAME);
    }

    @Override
    public void startFx(Stage stage) throws Exception {
        stage.setTitle("Ankor JavaFX Todo Sample");

        Pane myPane = FXMLLoader.load(getClass().getClassLoader().getResource("tasks.fxml"));

        Scene myScene = new Scene(myPane);
        myScene.getStylesheets().add("style.css");
        stage.setScene(myScene);
        stage.show();
    }

}
