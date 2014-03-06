package at.irian.ankorsamples.fxrates.server;

import at.irian.ankor.system.SocketServerStarter;

/**
 * @author Manfred Geiler
 */
public class RatesSocketServerStarter extends SocketServerStarter {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalsSocketServerStarter.class);

    public static void main(String[] args) {
        new RatesSocketServerStarter().start();
    }

    public RatesSocketServerStarter() {
        super(new RatesServerApplication());
    }
}
