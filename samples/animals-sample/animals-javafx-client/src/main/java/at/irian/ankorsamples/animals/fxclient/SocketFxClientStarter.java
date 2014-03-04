package at.irian.ankorsamples.animals.fxclient;

import at.irian.ankor.system.AnkorSystemBuilder;
import javafx.stage.Stage;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class SocketFxClientStarter extends javafx.application.Application {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketFxClientStarter.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Map<String,String> params = getParameters().getNamed();

        new AnkorSystemBuilder()
                //.withApplication(new AnimalsServerApplication())
                .createClient()
                .start();

        // todo    connect this client with the SocketParty(server, 8080) ...
    }
}
