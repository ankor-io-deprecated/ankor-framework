package at.irian.ankorsamples.fxrates.client;

import at.irian.ankor.action.Action;
import at.irian.ankor.fx.SocketFxClientApplication;
import at.irian.ankor.fx.binding.fxref.FxRefs;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * @author Manfred Geiler
 */
public class RatesSocketFxClientStarter extends SocketFxClientApplication {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalsSocketFxClientStarter.class);

    private static final String APPLICATION_NAME = "Rates JavaFX Client";
    private static final String MODEL_NAME = "root";

    public static void main(String[] args) {
        launch(args);
    }

    public RatesSocketFxClientStarter() {
        super(APPLICATION_NAME, MODEL_NAME);
    }

    @Override
    public void startFx(Stage stage) throws Exception {
        stage.setTitle("Ankor Rates JavaFX Sample");

        FxRefs.refFactory().ref("root").fire(new Action("init"));

        stage.setTitle("FX Rates Sample");
        Pane pane = FXMLLoader.load(getClass().getClassLoader().getResource("rates.fxml"));
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();

    }

}
