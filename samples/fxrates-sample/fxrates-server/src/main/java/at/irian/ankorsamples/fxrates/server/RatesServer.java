package at.irian.ankorsamples.fxrates.server;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.connection.ModelRootFactory;
import at.irian.ankor.socket.SocketAnkorSystemStarter;
import at.irian.ankor.socket.SocketMessageLoop;

import java.util.Collections;
import java.util.Set;

/**
 * @author Thomas Spiegl
 */
public class RatesServer {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Server.class);

    public static void main(String... args) {
        SocketAnkorSystemStarter appBuilder = new SocketAnkorSystemStarter()
                .withModelRootFactory(new MyModelRootFactory())
                .withLocalHost(parseHost("server@localhost:8080"));
        appBuilder.createAndStartServerSystem(false);

    }

    private static class MyModelRootFactory implements ModelRootFactory {
        @Override
        public Set<String> getKnownRootNames() {
            return Collections.singleton("root");
        }

        @Override
        public Object createModelRoot(Ref rootRef) {
            return new RatesViewModel(rootRef, new RatesRepository());
        }
    }

    private static SocketMessageLoop.Host parseHost(String systemIdAndHost) {
        String name = systemIdAndHost.split("@")[0];
        String hostAndPort = systemIdAndHost.split("@")[1];
        String hostname = hostAndPort.split(":")[0];
        int port = Integer.parseInt(hostAndPort.split(":")[1]);
        return new SocketMessageLoop.Host(name, hostname, port);
    }
}
