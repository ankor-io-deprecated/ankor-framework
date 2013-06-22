package at.irian.ankor.fx.app;

import at.irian.ankor.application.Application;
import at.irian.ankor.application.BeanResolver;
import at.irian.ankor.application.SimpleApplication;
import at.irian.ankor.service.SimpleAnkorServer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Spiegl
 */
public class SimpleLocalAppServiceCreator {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleAppServiceFactory.class);

    private Class<?> modelType = Object.class;
    private Map<String, Object> beans = new HashMap<String, Object>();
    private boolean serverStatusMessage;
    private SimpleApplication serverApplication;
    private SimpleApplication clientApplication;
    private AppService appService;

    public SimpleLocalAppServiceCreator withModelType(Class<?> modelType) {
        this.modelType = modelType;
        return this;
    }

    public SimpleLocalAppServiceCreator withBean(String beanName, Object bean) {
        this.beans.put(beanName, bean);
        return this;
    }

    public SimpleLocalAppServiceCreator withServerStatusMessage(boolean value) {
        serverStatusMessage = value;
        return this;
    }

    public void create() {
        serverApplication = SimpleApplication.create(modelType)
                                                        .withBeanResolver(new BeanResolver() {
                                                            @Override
                                                            public Object resolveByName(String beanName) {
                                                                return beans.get(beanName);
                                                            }
                                                        });
        SimpleAnkorServer server = SimpleAnkorServer.create(serverApplication, "server");
        server.start();

        clientApplication = SimpleApplication.create(modelType);
        SimpleAnkorServer client = SimpleAnkorServer.create(clientApplication, "client");
        client.start();

        server.setRemoteServer(client);
        client.setRemoteServer(server);

        if (serverStatusMessage) {
            startServerStatusThread(serverApplication);
        }

        appService = new AppService(clientApplication);
    }

    public SimpleApplication getServerApplication() {
        return serverApplication;
    }

    public SimpleApplication getClientApplication() {
        return clientApplication;
    }

    public AppService getAppService() {
        return appService;
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
