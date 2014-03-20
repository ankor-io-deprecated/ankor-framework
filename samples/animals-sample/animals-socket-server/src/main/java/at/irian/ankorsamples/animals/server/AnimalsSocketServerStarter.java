package at.irian.ankorsamples.animals.server;

import at.irian.ankor.system.SocketStandaloneServer;

/**
 * @author Manfred Geiler
 */
public class AnimalsSocketServerStarter {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalsSocketServerStarter.class);

    public static void main(String[] args) {
        new SocketStandaloneServer(new AnimalsServerApplication()).start();
    }

}
