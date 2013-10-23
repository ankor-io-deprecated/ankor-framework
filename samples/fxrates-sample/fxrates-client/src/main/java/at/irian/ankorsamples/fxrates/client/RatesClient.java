package at.irian.ankorsamples.fxrates.client;

import at.irian.ankor.action.Action;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.socket.SocketAnkorSystemStarter;
import at.irian.ankor.socket.SocketMessageLoop;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * @author Thomas Spiegl
 */
public class RatesClient extends javafx.application.Application {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Client.class);

    private static RefFactory refFactory;

    public static void main(String[] args) {
        // Ankor Client System
        SocketAnkorSystemStarter appBuilder = new SocketAnkorSystemStarter()
                .withLocalHost(parseHost("client@localhost:9090"))
                .withServerHost(parseHost("server@localhost:8080"));
        refFactory = appBuilder.createAndStartClientSystem();
        // Launch Java FX App
        launch(args);
    }

    public static Ref rootRef() {
        return refFactory.ref("root");
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Init Ankor Model
        refFactory.ref("root").fire(new Action("init"));

        stage.setTitle("FX Rates Sample");
        Pane pane = FXMLLoader.load(getClass().getClassLoader().getResource("rates.fxml"));
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
    }

    private static SocketMessageLoop.Host parseHost(String systemIdAndHost) {
        String name = systemIdAndHost.split("@")[0];
        String hostAndPort = systemIdAndHost.split("@")[1];
        String hostname = hostAndPort.split(":")[0];
        int port = Integer.parseInt(hostAndPort.split(":")[1]);
        return new SocketMessageLoop.Host(name, hostname, port);
    }
}
