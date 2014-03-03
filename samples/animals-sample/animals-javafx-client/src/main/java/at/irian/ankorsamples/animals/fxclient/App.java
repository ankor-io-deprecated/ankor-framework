package at.irian.ankorsamples.animals.fxclient;

import at.irian.ankor.application.SimpleSingleRootApplication;
import at.irian.ankor.event.dispatch.JavaFxEventDispatcherFactory;
import at.irian.ankor.fx.binding.fxref.FxRefContextFactoryProvider;
import at.irian.ankor.fx.binding.fxref.FxRefFactory;
import at.irian.ankor.fx.controller.AnkorFXMLLoader;
import at.irian.ankor.http.ClientHttpMessageLoop;
import at.irian.ankor.http.ServerHost;
import at.irian.ankor.messaging.json.viewmodel.ViewModelJsonMessageMapper;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.session.SingletonModelSessionManager;
import at.irian.ankor.socket.SocketAnkorSystemStarter;
import at.irian.ankor.socket.SocketMessageLoop;
import at.irian.ankor.system.AnkorSystem;
import at.irian.ankor.system.AnkorSystemBuilder;
import at.irian.ankor.websocket.WebSocketMessageBus;
import at.irian.ankor.websocket.WebSocketRemoteSystem;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static java.util.regex.Matcher.quoteReplacement;

/**
 * @author Thomas Spiegl
 */
public class App extends javafx.application.Application {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(App.class);

    private static final String DEFAULT_SERVER = "server@localhost:8080";
    private static final String DEFAULT_CLIENT = "client@localhost:9090";
    private static final int NUMBER_OF_CLIENTS = 3;

    private static FxRefFactory refFactory;

    private enum Mode {
        clientServer,
        client,
        server,
        manyClients,
        httpClient,
        webSocketClient
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
        } else if (mode == Mode.webSocketClient) {
            createWebSocketClientSystem(server);
            startFXClient(primaryStage);

        } else {
            stop();
        }
    }

    private void createWebSocketClientSystem(String server) throws Exception {
        final int HEARTBEAT_INTERVAL = 25;

        String host = server.split("@")[1];

        final String clientId = UUID.randomUUID().toString();
        final AnkorSystem[] clientSystem = new AnkorSystem[1];
        AnkorSystemBuilder systemBuilder = new AnkorSystemBuilder();
        final WebSocketMessageBus messageBus = null;// todo  new WebSocketMessageBus(new ViewModelJsonMessageMapper(systemBuilder.getBeanMetadataProvider()));
        final AnkorSystemBuilder finalSystemBuilder = systemBuilder
                .withName(clientId)
                //.withMessageBus(messageBus)
                .withDispatcherFactory(new JavaFxEventDispatcherFactory())
                //.withModelSessionId("collabTest") --> applicationInstance
                .withRefContextFactoryProvider(new FxRefContextFactoryProvider());

        // todo  register WebsocketEventMessageListener
        // todo  forard received websocket messages to MessageBus

        final CountDownLatch latch = new CountDownLatch(1);

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        String uri = "ws://" + host + "/websocket/ankor/" + clientId;

        container.connectToServer(new Endpoint() {

            @Override
            public void onOpen(Session session, EndpointConfig config) {
                session.addMessageHandler(new MessageHandler.Whole<String>() {

                    @Override
                    public void onMessage(String message) {
                        messageBus.receiveSerializedMessage(message);
                    }
                });

                messageBus.addRemoteSystem(new WebSocketRemoteSystem(clientId, session));
                (clientSystem[0] = finalSystemBuilder.createClient()).start();
                startHeartbeat(session);
                latch.countDown();
            }

            private Timer timer = new Timer();
            private void startHeartbeat(final Session session) {
                TimerTask task = new TimerTask() {

                    @Override
                    public void run() {
                        session.getAsyncRemote().sendText("");
                    }
                };

                timer.schedule(task, HEARTBEAT_INTERVAL * 1000, HEARTBEAT_INTERVAL * 1000);
            }

        }, URI.create(uri));

        if (latch.await(10, TimeUnit.SECONDS)) {

            ModelSession singletonModelSession = ((SingletonModelSessionManager) clientSystem[0].getModelSessionManager()).getModelSession();
            RefContext clientRefContext = singletonModelSession.getRefContext();
            refFactory = (FxRefFactory) clientRefContext.refFactory();
        } else {
            throw new Exception("WebSocket could not connect to " + uri);
        }
    }

    private void startFXClient(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Ankor FX Sample");

        AnkorFXMLLoader fxmlLoader = new AnkorFXMLLoader();
        fxmlLoader.setLocation(getClass().getClassLoader().getResource("main.fxml"));
        fxmlLoader.setResourcesRef(refFactory.ref("root.resources"));
        Pane myPane = (Pane) fxmlLoader.load();

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
                .withApplication(new MyApplication2())
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
                .withLocalHost(clientHost)
                .withServerHost(parseHost(server))
                .withRefContextFactoryProvider(new FxRefContextFactoryProvider());

        refFactory = (FxRefFactory) appBuilder.createAndStartClientSystem();

    }

    public static FxRefFactory refFactory() {
        return refFactory;
    }

    private static class MyApplication2 extends SimpleSingleRootApplication {
        public MyApplication2() {
            super("Animals", "root");
        }

        @Override
        public Object createRoot(RefContext refContext) {
            try {
                Class<?> modelRootType = Class.forName("at.irian.ankorsamples.animals.viewmodel.ModelRoot");
                Class<?> repoType = Class.forName("at.irian.ankorsamples.animals.domain.animal.AnimalRepository");
                Object repo = repoType.newInstance();
                Ref rootRef = refContext.refFactory().ref("root");
                return modelRootType.getConstructor(Ref.class, repoType).newInstance(rootRef, repo);
            } catch (Exception e) {
                throw new RuntimeException("Unable to create model root", e);
            }
        }
    }


    private void createHttpClientSystem(String client, String server) {

        String clientId = client.indexOf('@') >= 0 ? client.split("@")[0] : client;

        String serverId = server.split("@")[0];
        String serverUrl = server.split("@")[1];

        ClientHttpMessageLoop clientMessageLoop = new ClientHttpMessageLoop(client, new ServerHost(serverId, serverUrl));

        AnkorSystem clientSystem = new AnkorSystemBuilder()
                .withName(clientId)
                //.withMessageBus(clientMessageLoop.getMessageBus())
                //.withModelSessionId("collabTest") --> applicationInstance
                .withDispatcherFactory(new JavaFxEventDispatcherFactory())
                .withRefContextFactoryProvider(new FxRefContextFactoryProvider())
                .createClient();

        // todo  register HttpPollingEventMessageListener
        // todo  forard received http messages to MessageBus

        // start
        clientSystem.start();
        clientMessageLoop.start(true);

        ModelSession singletonModelSession = ((SingletonModelSessionManager) clientSystem.getModelSessionManager()).getModelSession();
        RefContext clientRefContext = singletonModelSession.getRefContext();
        refFactory = (FxRefFactory) clientRefContext.refFactory();
    }


}