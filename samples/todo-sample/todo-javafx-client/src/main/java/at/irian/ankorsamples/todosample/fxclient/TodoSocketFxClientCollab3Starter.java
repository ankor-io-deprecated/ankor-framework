package at.irian.ankorsamples.todosample.fxclient;

import at.irian.ankor.system.AnkorClient;
import at.irian.ankor.system.SocketFxClientBuilder;

/**
 * @author Manfred Geiler
 */
public class TodoSocketFxClientCollab3Starter extends TodoSocketFxClientStarter {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TodoSocketFxClientCollab1Starter.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected AnkorClient createAnkorClient() {
        return new SocketFxClientBuilder().withApplicationName(APPLICATION_NAME + " 3")
                                          .withModelName(MODEL_NAME)
                                          .withConnectParam("todoListId", "collabTest")
                                          .withClientAddress("//localhost:9093")
                                          .build();
    }
}
