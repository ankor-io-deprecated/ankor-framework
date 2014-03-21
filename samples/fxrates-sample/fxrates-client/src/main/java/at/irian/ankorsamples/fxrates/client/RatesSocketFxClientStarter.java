package at.irian.ankorsamples.fxrates.client;

import at.irian.ankor.action.Action;
import at.irian.ankor.fx.binding.fxref.FxRefs;
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
public class RatesSocketFxClientStarter extends Application {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalsSocketFxClientStarter.class);

    private static final String APPLICATION_NAME = "Rates JavaFX Client";
    private static final String MODEL_NAME = "root";

    public static void main(String[] args) {
        launch(args);
    }

    private AnkorClient ankorClient;

    @Override
    public void start(Stage stage) throws Exception {
        ankorClient = SocketFxClient.create(APPLICATION_NAME, MODEL_NAME, getParameters());
        ankorClient.start();

        stage.setTitle("Ankor Rates JavaFX Sample");

        FxRefs.refFactory().ref("root").fire(new Action("init"));

        stage.setTitle("FX Rates Sample");
        Pane pane = FXMLLoader.load(getClass().getClassLoader().getResource("rates.fxml"));
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        ankorClient.stop();
        super.stop();
    }
}
