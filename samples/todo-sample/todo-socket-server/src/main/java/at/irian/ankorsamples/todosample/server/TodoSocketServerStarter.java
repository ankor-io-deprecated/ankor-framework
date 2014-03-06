package at.irian.ankorsamples.todosample.server;

import at.irian.ankor.system.SocketServerStarter;

/**
 * @author Manfred Geiler
 */
public class TodoSocketServerStarter extends SocketServerStarter {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalsSocketServerStarter.class);

    public static void main(String[] args) {
        new TodoSocketServerStarter().start();
    }

    public TodoSocketServerStarter() {
        super(new TodoServerApplication());
    }
}
