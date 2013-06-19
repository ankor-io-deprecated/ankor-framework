package at.irian.ankor.fx.app;

import at.irian.ankor.core.application.Application;
import at.irian.ankor.core.application.SimpleApplication;
import at.irian.ankor.core.server.SimpleAnkorServer;
import at.irian.ankor.sample.fx.view.model.RootModel;

/**
 * @author Thomas Spiegl
 */
public class SimpleLocalApplicationService {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleAppServiceFactory.class);

    private String beanName;
    private Object bean;
    private boolean serverStatusMessage;

    public SimpleLocalApplicationService withBean(String beanName, Object bean) {
        this.beanName = beanName;
        this.bean = bean;
        return this;
    }

    public SimpleLocalApplicationService withServerStatusMessage(boolean value) {
        serverStatusMessage = value;
        return this;
    }

    public AppService create() {
        final Application serverApp = SimpleApplication.withModelType(RootModel.class)
                .withBean(beanName, bean);
        SimpleAnkorServer server = new SimpleAnkorServer(serverApp, "server");
        server.start();

        Application clientApp = SimpleApplication.withModelType(RootModel.class);
        SimpleAnkorServer client = new SimpleAnkorServer(clientApp, "client");
        client.start();

        server.setRemoteServer(client);
        client.setRemoteServer(server);

        if (serverStatusMessage) {
            startServerStatusThread(serverApp);
        }

        return new AppService(clientApp);
    }

    public void startServerStatusThread(final Application serverApplication) {
        final long started = System.currentTimeMillis();
        new Thread(new Runnable() {
            public void run() {
                //noinspection InfiniteLoopStatement
                for (;;) {
                    try {
                        Thread.sleep(1000 * 30);
                    } catch (InterruptedException e) {
                        return;
                    }

                    long upSinceSeconds = (System.currentTimeMillis() - started) / 1000;
                    String serverStatus = String.format("sever up time %ds", upSinceSeconds);

                    serverApplication.getRefFactory().rootRef().sub("serverStatus").setValue(serverStatus);
                }
            }
        }).start();
    }
}
