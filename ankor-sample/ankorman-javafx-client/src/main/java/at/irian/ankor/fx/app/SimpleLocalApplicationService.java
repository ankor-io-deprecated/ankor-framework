package at.irian.ankor.fx.app;

import at.irian.ankor.application.Application;
import at.irian.ankor.application.DefaultApplication;
import at.irian.ankor.application.SimpleApplication;
import at.irian.ankor.sample.fx.server.ServiceBean;
import at.irian.ankor.sample.fx.view.model.ModelRoot;
import at.irian.ankor.service.SimpleAnkorServer;

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
        final DefaultApplication serverApp = SimpleApplication.create(ModelRoot.class)
                .withBean(beanName, bean);
        SimpleAnkorServer server = SimpleAnkorServer.create(serverApp, "server");
        server.start();
        ((ServiceBean) bean).setApplication(serverApp); // TODO hack

        DefaultApplication clientApp = SimpleApplication.create(ModelRoot.class);
        SimpleAnkorServer client = SimpleAnkorServer.create(clientApp, "client");
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
                    String serverStatus = String.format("server up time %ds", upSinceSeconds);

                    serverApplication.getRefFactory().rootRef().sub("serverStatus").setValue(serverStatus);
                }
            }
        }).start();
    }
}
