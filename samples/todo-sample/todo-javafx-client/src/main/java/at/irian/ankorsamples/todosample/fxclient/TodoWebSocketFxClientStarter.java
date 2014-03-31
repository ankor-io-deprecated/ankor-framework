package at.irian.ankorsamples.todosample.fxclient;

import at.irian.ankor.system.AnkorClient;
import at.irian.ankor.system.WebSocketEndpointListener;
import at.irian.ankor.system.WebSocketFxClient;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

public class TodoWebSocketFxClientStarter extends javafx.application.Application {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TodoWebSocketFxClientStarter.class);

    // used to open the browser
    private static HostServices services;

    private AnkorClient client;

    private EndpointListener endpointListener;

    public static void main(String[] args) {
        launch(args);
    }

    public TodoWebSocketFxClientStarter() throws Exception {
        serverStatus = new Label();
        endpointListener = new EndpointListener();
        client = WebSocketFxClient.builder()
                .withApplicationName("Todo FX Client")
                .withModelName("root")
                .withConnectParam("todoListId", "collaborationTest")
                .withServer("localhost", 8080, "/websocket/ankor")
                .withEndpointListener(endpointListener)
                .build();
    }

    @Override
    public void start(Stage stage) throws Exception {
        client.start();
        services = getHostServices();

        stage.setTitle("Ankor JavaFX Todo Sample");
        Pane myPane = FXMLLoader.load(getClass().getClassLoader().getResource("tasks.fxml"));

        Scene myScene = new Scene(myPane);
        myScene.getStylesheets().add("style.css");

        serverStatus = (Label) myPane.lookup("#serverStatus");
        endpointListener.updateServerStatusLabelText();

        stage.setScene(myScene);
        stage.show();
    }

    private Label serverStatus;


    @Override
    public void stop() throws Exception {
        client.stop();
        super.stop();
    }

    public static HostServices getServices() {
        return services;
    }

    private class EndpointListener implements WebSocketEndpointListener {

        private String status = "Server Connection: not connected";

        @Override
        public void onOpen(Session session, EndpointConfig config) {
            updateStatus("Server Connection: ok");
        }

        @Override
        public void onClose(Session session, CloseReason closeReason) {
            updateStatus("Server Connection: closed");
        }

        @Override
        public void onError(Session session, Throwable thr) {
            updateStatus("Server Connection: error");
        }

        private void updateStatus(final String status) {
            this.status = status;
            if (serverStatus != null) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        updateServerStatusLabelText();
                    }
                });
            }
        }

        public void updateServerStatusLabelText() {
            if (status.endsWith("ok")) {
                serverStatus.setStyle("-fx-text-fill: green");
            } else {
                serverStatus.setStyle("-fx-text-fill: red");
            }
            serverStatus.setText(status);
        }
    }

}