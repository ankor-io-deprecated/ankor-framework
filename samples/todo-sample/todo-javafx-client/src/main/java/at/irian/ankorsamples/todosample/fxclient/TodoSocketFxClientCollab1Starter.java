package at.irian.ankorsamples.todosample.fxclient;

import at.irian.ankor.system.AnkorClient;
import at.irian.ankor.system.SocketFxClient;

/**
 * @author Manfred Geiler
 */
public class TodoSocketFxClientCollab1Starter extends TodoSocketFxClientStarter {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TodoSocketFxClientCollab1Starter.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected AnkorClient createAnkorClient() {
        return SocketFxClient.create(APPLICATION_NAME + " 1", MODEL_NAME, "collabTest", "//localhost:9091");
    }
}
