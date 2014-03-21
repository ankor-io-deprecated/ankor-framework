package at.irian.ankorsamples.fxrates.server;

import at.irian.ankor.system.SocketStandaloneServer;

/**
 * @author Manfred Geiler
 */
public class RatesSocketServerStarter {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalsSocketServerStarter.class);

    public static void main(String[] args) {
        new SocketStandaloneServer(new RatesServerApplication()).start();
    }

}
