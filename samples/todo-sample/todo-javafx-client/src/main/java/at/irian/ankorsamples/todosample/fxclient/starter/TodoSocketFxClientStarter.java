package at.irian.ankorsamples.todosample.fxclient.starter;

import at.irian.ankor.system.AnkorClient;
import at.irian.ankor.system.SocketFxClient;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * @author Manfred Geiler
 */
public class TodoSocketFxClientStarter extends javafx.application.Application {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TodoSocketFxClientStarter.class);

    protected static final String APPLICATION_NAME = "Ankor Todo Socket FX Client";
    protected static final String MODEL_NAME = "root";
    protected static final String MODEL_INSTANCE_ID = "collaborationTest";


    public static void main(String[] args) {
        launch(args);
    }

    // used to open the browser
    private static HostServices services;

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

        services = getHostServices();

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

    public static HostServices getServices() {
        return services;
    }
}
