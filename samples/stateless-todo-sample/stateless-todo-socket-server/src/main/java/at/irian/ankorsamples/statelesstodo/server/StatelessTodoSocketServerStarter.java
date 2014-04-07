package at.irian.ankorsamples.statelesstodo.server;

import at.irian.ankor.system.SocketStatelessStandaloneServer;

/**
 * @author Manfred Geiler
 */
public class StatelessTodoSocketServerStarter {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalsSocketServerStarter.class);

    public static void main(String[] args) {
        new SocketStatelessStandaloneServer(new StatelessTodoServerApplication()).start();
    }

}
