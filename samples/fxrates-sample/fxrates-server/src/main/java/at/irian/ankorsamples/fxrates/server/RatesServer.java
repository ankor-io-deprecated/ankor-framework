package at.irian.ankorsamples.fxrates.server;

import at.irian.ankor.application.SimpleSingleRootApplication;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.socket.SocketAnkorSystemStarter;
import at.irian.ankor.socket.SocketMessageLoop;

/**
 * @author Thomas Spiegl
 */
public class RatesServer {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Server.class);

    public static void main(String... args) {
        SocketAnkorSystemStarter appBuilder = new SocketAnkorSystemStarter()
                .withApplication(new SimpleSingleRootApplication("rates", "root") {
                    @Override
                    public Object createRoot(Ref rootRef) {
                        return new RatesViewModel(rootRef, new RatesRepository());
                    }
                })
                .withLocalHost(parseHost("server@localhost:8080"));
        appBuilder.createAndStartServerSystem(false);

    }

    private static SocketMessageLoop.Host parseHost(String systemIdAndHost) {
        String name = systemIdAndHost.split("@")[0];
        String hostAndPort = systemIdAndHost.split("@")[1];
        String hostname = hostAndPort.split(":")[0];
        int port = Integer.parseInt(hostAndPort.split(":")[1]);
        return new SocketMessageLoop.Host(name, hostname, port);
    }
}
