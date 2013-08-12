package at.irian.ankor.sample1.fxclient;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.session.ModelRootFactory;
import at.irian.ankor.system.SocketAnkorSystemStarter;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Map;

/**
 * @author Thomas Spiegl
 */
public class App extends javafx.application.Application {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(App.class);

    private static final String DEFAULT_SERVER = "server:8080";
    private static final String DEFAULT_CLIENT = "client:9090";

    private static RefFactory refFactory;

    private enum Mode {
        clientServer,
        client,
        server
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Map<String,String> params = getParameters().getNamed();

        Mode mode = Mode.clientServer;
        String modeParam = params.get("mode");
        if (modeParam != null) {
            mode = Mode.valueOf(modeParam);
        }

        String server = params.get("server");
        if (server == null) {
            server = DEFAULT_SERVER;
        }


        if (mode == Mode.clientServer || mode == Mode.server) {
            String clients = params.get("clients");
            if (clients == null) {
                clients = DEFAULT_CLIENT;
            }
            createServerSystem(server, clients);
        }

        if (mode == Mode.clientServer || mode == Mode.client) {
            String client = params.get("client");
            if (client == null) {
                client = DEFAULT_CLIENT;
            }
            createClientSystem(server, client);

            primaryStage.setTitle("Ankor FX Sample");
            Pane myPane = FXMLLoader.load(getClass().getClassLoader().getResource("main.fxml"));

            Scene myScene = new Scene(myPane);
            myScene.getStylesheets().add("style.css");
            primaryStage.setScene(myScene);
            primaryStage.show();
        }
    }


    private void createServerSystem(String server, String clients) {

        String serverName = server.split(":")[0];
        int serverPort = Integer.parseInt(server.split(":")[1]);

        SocketAnkorSystemStarter appBuilder = new SocketAnkorSystemStarter()
                .withModelRootFactory(new MyModelRootFactory())
                .withLocalServer(serverName, serverPort);

        for (String client : clients.split(",")) {
            String clientName = client.split(":")[0];
            int clientPort = Integer.parseInt(client.split(":")[1]);
            appBuilder = appBuilder.withLocalClient(clientName, clientPort);
        }

        appBuilder.createAndStartServerSystem();
    }

    private void createClientSystem(String server, String client) {

        String serverName = server.split(":")[0];
        int serverPort = Integer.parseInt(server.split(":")[1]);

        String clientName = client.split(":")[0];
        int clientPort = Integer.parseInt(client.split(":")[1]);

        SocketAnkorSystemStarter appBuilder = new SocketAnkorSystemStarter()
                .withModelRootFactory(new MyModelRootFactory())
                .withLocalServer(serverName, serverPort)
                .withLocalClient(clientName, clientPort);

        refFactory = appBuilder.createAndStartClientSystem(clientName);
    }

    public static RefFactory refFactory() {
        return refFactory;
    }



    private static class MyModelRootFactory implements ModelRootFactory {
        @Override
        public Object createModelRoot(Ref rootRef) {
            try {
                Class<?> modelRootType = Class.forName("at.irian.ankor.sample1.viewmodel.ModelRoot");
                Class<?> repoType = Class.forName("at.irian.ankor.sample1.server.AnimalRepository");
                Object repo = repoType.newInstance();
                return modelRootType.getConstructor(Ref.class, repoType).newInstance(rootRef, repo);
            } catch (Exception e) {
                throw new RuntimeException("Unable to create model root", e);
            }
        }
    }
}