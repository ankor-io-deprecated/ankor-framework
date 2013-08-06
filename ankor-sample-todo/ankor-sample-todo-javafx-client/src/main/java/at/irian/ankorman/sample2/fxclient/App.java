package at.irian.ankorman.sample2.fxclient;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.session.ModelRootFactory;
import at.irian.ankor.system.SocketAnkorSystemStarter; // TODO: no longer available in master branch
import at.irian.ankorman.sample2.server.AnimalRepository;
import at.irian.ankorman.sample2.server.TaskRepository;
import at.irian.ankorman.sample2.viewmodel.ModelRoot;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * @author Thomas Spiegl
 */
public class App extends javafx.application.Application {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(App.class);

    private static final String DEFAULT_SERVER = "server:8080";
    private static final String DEFAULT_CLIENT = "client:9090";

    private static RefFactory refFactory;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // server
        String server = DEFAULT_SERVER;
        String clients = DEFAULT_CLIENT;

        createServerSystem(server, clients);

        // client
        String client = DEFAULT_CLIENT;

        createClientSystem(server, client);

        primaryStage.setTitle("Ankor FX Sample");
        Pane myPane = FXMLLoader.load(getClass().getClassLoader().getResource("main.fxml"));

        Scene myScene = new Scene(myPane);
        myScene.getStylesheets().add("style.css");
        primaryStage.setScene(myScene);
        primaryStage.show();
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
            return new ModelRoot(rootRef, new AnimalRepository(), new TaskRepository());
        }
    }
}