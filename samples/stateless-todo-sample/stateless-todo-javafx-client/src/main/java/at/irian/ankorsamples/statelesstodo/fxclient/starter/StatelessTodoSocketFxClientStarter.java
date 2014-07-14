package at.irian.ankorsamples.statelesstodo.fxclient.starter;

import at.irian.ankor.system.AnkorClient;
import at.irian.ankor.system.SocketFxClient;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * @author Manfred Geiler
 */
public class StatelessTodoSocketFxClientStarter extends Application {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(StatelessTodoSocketFxClientStarter.class);

    protected static final String APPLICATION_NAME = "Ankor Stateless Todo Socket FX Client";
    protected static final String MODEL_NAME = "root";

    public static void main(String[] args) {
        launch(args);
    }

    private AnkorClient ankorClient;

    protected AnkorClient createAnkorClient() {
        return SocketFxClient.builder()
                .withApplicationName(APPLICATION_NAME)
                .withModelName(MODEL_NAME)
                .build();
    }

    @Override
    public void start(Stage stage) throws Exception {
        ankorClient = createAnkorClient();
        ankorClient.start();

        stage.setTitle(APPLICATION_NAME);
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
