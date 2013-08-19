package at.irian.ankor.todosample.fxclient;

import at.irian.ankor.fx.controller.FXControllerChangeListener;
import at.irian.ankor.http.ClientHttpMessageLoop;
import at.irian.ankor.http.ServerHost;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.session.ModelRootFactory;
import at.irian.ankor.session.SingletonSessionManager;
import at.irian.ankor.socket.SocketAnkorSystemStarter;
import at.irian.ankor.socket.SocketMessageLoop;
import at.irian.ankor.system.AnkorSystem;
import at.irian.ankor.system.AnkorSystemBuilder;
import at.irian.ankor.todosample.server.TaskRepository;
import at.irian.ankor.todosample.viewmodel.ModelRoot;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

import static java.util.regex.Matcher.quoteReplacement;

/**
 * @author Thomas Spiegl
 */
public class App extends javafx.application.Application {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(App.class);

    private static final String DEFAULT_SERVER = "server@localhost:8080";
    private static final String DEFAULT_CLIENT = "client@localhost:9090";
    private static final int NUMBER_OF_CLIENTS = 3;

    private static RefFactory refFactory;

    private enum Mode {
        clientServer,
        client,
        server,
        manyClients,
        httpClient
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

        if (mode == Mode.manyClients) {
            String[] command = {replaceVars("${java.home}/bin/java"),
                                "-classpath",
                                '"' + System.getProperty("java.class.path") + replaceVars(
                                            ":${project.home}/ankor-sample/ankorman-javafx-client/target/classes" +
                                            ":${project.home}/ankor-fx/target/classes" +
                                            ":${project.home}/ankor-core/target/classes" +
                                            ":${maven.repo}/org/slf4j/slf4j-api/1.7.1/slf4j-api-1.7.1.jar" +
                                            ":${maven.repo}/com/typesafe/config/0.3.1/config-0.3.1.jar" +
                                            ":${project.home}/ankor-actor/target/classes" +
                                            ":${maven.repo}/com/typesafe/akka/akka-actor_2.10/2.2.0/akka-actor_2.10-2.2.0.jar" +
                                            ":${maven.repo}/org/scala-lang/scala-library/2.10.2/scala-library-2.10.2.jar" +
                                            ":${project.home}/ankor-service/target/classes:${project.home}/ankor-el/target/classes" +
                                            ":${maven.repo}/javax/el/el-api/2.2/el-api-2.2.jar" +
                                            ":${maven.repo}/org/glassfish/web/el-impl/2.2/el-impl-2.2.jar" +
                                            ":${project.home}/ankor-json/target/classes" +
                                            ":${maven.repo}/com/fasterxml/jackson/core/jackson-databind/2.2.2/jackson-databind-2.2.2.jar" +
                                            ":${maven.repo}/com/fasterxml/jackson/core/jackson-annotations/2.2.2/jackson-annotations-2.2.2.jar" +
                                            ":${maven.repo}/com/fasterxml/jackson/core/jackson-core/2.2.2/jackson-core-2.2.2.jar" +
                                            ":${project.home}/ankor-annotation/target/classes" +
                                            ":${project.home}/ankor-sample/ankorman-server/target/classes" +
                                            ":${maven.repo}/ch/qos/logback/logback-classic/1.0.7/logback-classic-1.0.7.jar" +
                                            ":${maven.repo}/ch/qos/logback/logback-core/1.0.7/logback-core-1.0.7.jar") + '"',
                                App.class.getName(),
                                "--mode=client",
                                "--client=c1@localhost:9090"};
            Process lastProcess = null;
            for (int i = 0; i < NUMBER_OF_CLIENTS; i++) {
                String[] c = new String[6];
                System.arraycopy(command, 0, c, 0, 5);
                c[5] = String.format("--client=c%03d@localhost:9%03d", i, i);
                lastProcess = new ProcessBuilder().command(c)
                                                  .inheritIO()
                                                  .start();
            }
            if (lastProcess != null) {
                lastProcess.waitFor();
            }
            System.exit(0);
        }

        String server = params.get("server");
        if (server == null) {
            server = DEFAULT_SERVER;
        }

        if (mode == Mode.clientServer || mode == Mode.server) {
            createServerSystem(server, mode == Mode.clientServer);
        }

        if (mode == Mode.clientServer || mode == Mode.client) {
            String client = params.get("client");
            if (client == null) {
                client = DEFAULT_CLIENT;
            }
            createClientSystem(client, server);
            startFXClient(primaryStage);
        } else if (mode == Mode.httpClient) {
            String client = params.get("client");
            if (client == null) {
                client = DEFAULT_CLIENT;
            }
            createHttpClientSystem(client, server);
            startFXClient(primaryStage);
        } else {
            stop();
        }
    }

    private void startFXClient(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Ankor FX Sample");
            Pane myPane = FXMLLoader.load(getClass().getClassLoader().getResource("tasks.fxml"));

        Scene myScene = new Scene(myPane);
        myScene.getStylesheets().add("style.css");
        primaryStage.setScene(myScene);
        primaryStage.show();
    }

    private String replaceVars(String s) {
        s = s.replaceAll(":", quoteReplacement(System.getProperty("path.separator")));
        s = s.replaceAll("\\$\\{java\\.home\\}", quoteReplacement(System.getProperty("java.home")));
        s = s.replaceAll("\\$\\{project\\.home\\}", quoteReplacement(System.getProperty("user.dir")));
        s = s.replaceAll("\\$\\{maven\\.repo\\}", quoteReplacement(System.getProperty("user.home") + "/.m2/repository"));
        return s;
    }


    private void createServerSystem(String server, boolean daemon) {

        SocketAnkorSystemStarter appBuilder = new SocketAnkorSystemStarter()
                .withModelRootFactory(new MyModelRootFactory())
                .withLocalHost(parseHost(server));

        appBuilder.createAndStartServerSystem(daemon);
    }

    private SocketMessageLoop.Host parseHost(String systemIdAndHost) {
        String name = systemIdAndHost.split("@")[0];
        String hostAndPort = systemIdAndHost.split("@")[1];
        String hostname = hostAndPort.split(":")[0];
        int port = Integer.parseInt(hostAndPort.split(":")[1]);
        return new SocketMessageLoop.Host(name, hostname, port);
    }

    private void createClientSystem(String client, String server) {

        SocketMessageLoop.Host clientHost = parseHost(client);

        SocketAnkorSystemStarter appBuilder = new SocketAnkorSystemStarter()
                .withModelRootFactory(new MyModelRootFactory())
                .withLocalHost(clientHost)
                .withGlobalEventListener(new FXControllerChangeListener())
                .withServerHost(parseHost(server));

        refFactory = appBuilder.createAndStartClientSystem();

    }

    public static RefFactory refFactory() {
        return refFactory;
    }

    private static class MyModelRootFactory implements ModelRootFactory {
        @Override
        public Object createModelRoot(Ref rootRef) {
            return new ModelRoot(rootRef, new TaskRepository());
        }
    }

    private void createHttpClientSystem(String client, String server) {

        String clientId = client.indexOf('@') >= 0 ? client.split("@")[0] : client;

        String serverId = server.split("@")[0];
        String serverUrl = server.split("@")[1];

        ClientHttpMessageLoop clientMessageLoop = new ClientHttpMessageLoop(client, new ServerHost(serverId, serverUrl));

        AnkorSystem clientSystem = new AnkorSystemBuilder()
                .withName(clientId)
                .withGlobalEventListener(new FXControllerChangeListener())
                .withMessageBus(clientMessageLoop.getMessageBus())
                .withModelContextId("collabTest")
                .createClient();

        // start
        clientSystem.start();
        clientMessageLoop.start(true);

        RefContext clientRefContext = ((SingletonSessionManager)clientSystem.getSessionManager()).getSession().getRefContext();
        refFactory = clientRefContext.refFactory();
    }


}