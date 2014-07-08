package at.irian.ankorsamples.todosample.fxclient.starter;

import at.irian.ankor.system.AnkorClient;
import at.irian.ankor.system.SocketFxClient;

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
        return SocketFxClient.builder()
                .withApplicationName(APPLICATION_NAME + " 3")
                .withModelName(MODEL_NAME)
                .withConnectParam("at.irian.ankor.MODEL_INSTANCE_ID", MODEL_INSTANCE_ID)
                .withClientAddress("//localhost:9093")
                .build();
    }
}
