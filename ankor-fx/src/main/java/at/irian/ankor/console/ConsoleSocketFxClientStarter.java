package at.irian.ankor.console;

import at.irian.ankor.system.AnkorClient;
import at.irian.ankor.system.SocketFxClient;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * @author Manfred Geiler
 */
public class ConsoleSocketFxClientStarter extends javafx.application.Application {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ConsoleWebSocketFxClientStarter.class);

    public static void main(String[] args) {

        System.setProperty("logback.configurationFile", "at/irian/ankor/console/logback.xml");

        launch(args);
    }


    private AnkorClient client;

    @Override
    public void start(Stage stage) throws Exception {
        client = SocketFxClient.create("Console FX Client", ConsoleApplication.MODEL_NAME,
                                       null, "//localhost:9000", "//localhost:8080");
        client.start();

        stage.setTitle("Ankor JavaFX Console");
        Pane myPane = FXMLLoader.load(getClass().getClassLoader().getResource("at/irian/ankor/console/console.fxml"));

        Scene myScene = new Scene(myPane);
        myScene.getStylesheets().add("at/irian/ankor/console/style.css");

        stage.setScene(myScene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        client.stop();
        super.stop();
    }

}
