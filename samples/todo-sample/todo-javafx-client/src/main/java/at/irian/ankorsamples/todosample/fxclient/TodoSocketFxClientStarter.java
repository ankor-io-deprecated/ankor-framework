package at.irian.ankorsamples.todosample.fxclient;

import at.irian.ankor.system.AnkorClient;
import at.irian.ankor.system.SocketFxClientBuilder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * @author Manfred Geiler
 */
public class TodoSocketFxClientStarter extends Application {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TodoSocketFxClientStarter.class);

    protected static final String APPLICATION_NAME = "Todo Sample FX Client";
    protected static final String MODEL_NAME = "root";

    public static void main(String[] args) {
        launch(args);
    }

    private AnkorClient ankorClient;

    protected AnkorClient createAnkorClient() {
        return new SocketFxClientBuilder().withApplicationName(APPLICATION_NAME)
                                          .withModelName(MODEL_NAME)
                                          .build();
    }

    @Override
    public void start(Stage stage) throws Exception {
        ankorClient = createAnkorClient();
        ankorClient.start();

        stage.setTitle("Ankor JavaFX Todo Sample");
        Pane myPane = FXMLLoader.load(getClass().getClassLoader().getResource("tasks.fxml"));
        Scene myScene = new Scene(myPane);
        myScene.getStylesheets().add("style.css");
        stage.setScene(myScene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        ankorClient.stop();
        super.stop();
    }

}
