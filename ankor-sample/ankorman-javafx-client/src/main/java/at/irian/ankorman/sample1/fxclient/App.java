package at.irian.ankorman.sample1.fxclient;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.session.ModelRootFactory;
import at.irian.ankor.socket.SocketAnkorSystemStarter;
import at.irian.ankor.socket.SocketMessageLoop;
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

    private static final String DEFAULT_SERVER = "server@localhost:8080";
    private static final String DEFAULT_CLIENT = "client@localhost:9090";

    private static RefFactory refFactory;

    private enum Mode {
        clientServer,
        client,
        server,
        manyClients
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
            String[] command = {"/Library/Java/JavaVirtualMachines/jdk1.7.0_07.jdk/Contents/Home/bin/java",
                                "-classpath",
                                "\"/Library/Java/JavaVirtualMachines/jdk1.7.0_07.jdk/Contents/Home/lib/ant-javafx.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_07.jdk/Contents/Home/lib/dt.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_07.jdk/Contents/Home/lib/javafx-doclet.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_07.jdk/Contents/Home/lib/javafx-mx.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_07.jdk/Contents/Home/lib/jconsole.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_07.jdk/Contents/Home/lib/sa-jdi.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_07.jdk/Contents/Home/lib/tools.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_07.jdk/Contents/Home/jre/lib/charsets.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_07.jdk/Contents/Home/jre/lib/deploy.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_07.jdk/Contents/Home/jre/lib/htmlconverter.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_07.jdk/Contents/Home/jre/lib/javaws.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_07.jdk/Contents/Home/jre/lib/jce.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_07.jdk/Contents/Home/jre/lib/jfr.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_07.jdk/Contents/Home/jre/lib/jfxrt.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_07.jdk/Contents/Home/jre/lib/JObjC.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_07.jdk/Contents/Home/jre/lib/jsse.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_07.jdk/Contents/Home/jre/lib/management-agent.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_07.jdk/Contents/Home/jre/lib/plugin.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_07.jdk/Contents/Home/jre/lib/resources.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_07.jdk/Contents/Home/jre/lib/rt.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_07.jdk/Contents/Home/jre/lib/ext/dnsns.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_07.jdk/Contents/Home/jre/lib/ext/localedata.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_07.jdk/Contents/Home/jre/lib/ext/sunec.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_07.jdk/Contents/Home/jre/lib/ext/sunjce_provider.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_07.jdk/Contents/Home/jre/lib/ext/sunpkcs11.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_07.jdk/Contents/Home/jre/lib/ext/zipfs.jar:/Users/manolito/Develop/Irian/edm/edm/ankor-sample/ankorman-javafx-client/target/classes:/Users/manolito/Develop/Irian/edm/edm/ankor-fx/target/classes:/Users/manolito/Develop/Irian/edm/edm/ankor-core/target/classes:/Users/manolito/.m2/repository/org/slf4j/slf4j-api/1.7.1/slf4j-api-1.7.1.jar:/Users/manolito/.m2/repository/com/typesafe/config/0.3.1/config-0.3.1.jar:/Users/manolito/Develop/Irian/edm/edm/ankor-actor/target/classes:/Users/manolito/.m2/repository/com/typesafe/akka/akka-actor_2.10/2.2.0/akka-actor_2.10-2.2.0.jar:/Users/manolito/.m2/repository/org/scala-lang/scala-library/2.10.2/scala-library-2.10.2.jar:/Users/manolito/Develop/Irian/edm/edm/ankor-service/target/classes:/Users/manolito/Develop/Irian/edm/edm/ankor-el/target/classes:/Users/manolito/.m2/repository/javax/el/el-api/2.2/el-api-2.2.jar:/Users/manolito/.m2/repository/org/glassfish/web/el-impl/2.2/el-impl-2.2.jar:/Users/manolito/Develop/Irian/edm/edm/ankor-json/target/classes:/Users/manolito/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.2.2/jackson-databind-2.2.2.jar:/Users/manolito/.m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.2.2/jackson-annotations-2.2.2.jar:/Users/manolito/.m2/repository/com/fasterxml/jackson/core/jackson-core/2.2.2/jackson-core-2.2.2.jar:/Users/manolito/Develop/Irian/edm/edm/ankor-annotation/target/classes:/Users/manolito/Develop/Irian/edm/edm/ankor-sample/ankorman-server/target/classes:/Users/manolito/.m2/repository/ch/qos/logback/logback-classic/1.0.7/logback-classic-1.0.7.jar:/Users/manolito/.m2/repository/ch/qos/logback/logback-core/1.0.7/logback-core-1.0.7.jar:/Applications/IntelliJ IDEA 12.app/lib/idea_rt.jar\"",
                                "at.irian.ankorman.sample1.fxclient.App",
                                "--mode=client",
                                "--client=c1@localhost:9090"};
            Process lastProcess = null;
            for (int i = 0; i < 10; i++) {
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

            primaryStage.setTitle("Ankor FX Sample");
            Pane myPane = FXMLLoader.load(getClass().getClassLoader().getResource("main.fxml"));

            Scene myScene = new Scene(myPane);
            myScene.getStylesheets().add("style.css");
            primaryStage.setScene(myScene);
            primaryStage.show();
        } else {
            stop();
        }
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
                .withServerHost(parseHost(server));

        refFactory = appBuilder.createAndStartClientSystem();
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