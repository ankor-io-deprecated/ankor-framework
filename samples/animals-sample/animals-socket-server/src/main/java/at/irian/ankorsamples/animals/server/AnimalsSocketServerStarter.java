package at.irian.ankorsamples.animals.server;

import at.irian.ankor.system.SocketServerStarter;

/**
 * @author Manfred Geiler
 */
public class AnimalsSocketServerStarter extends SocketServerStarter {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalsSocketServerStarter.class);

    public static void main(String[] args) {
        new AnimalsSocketServerStarter().start();
    }

    public AnimalsSocketServerStarter() {
        super(new AnimalsServerApplication());
    }
}
