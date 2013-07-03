package at.irian.ankor.fx.app;

import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.system.AnkorSystem;
import at.irian.ankor.system.BeanResolver;
import at.irian.ankor.system.SocketAnkorSystem;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Spiegl
 */
public class SocketAppServiceBuilder {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleAppServiceFactory.class);

    private static final String HOST = "localhost";
    private static final int serverPort = 8080;
    private static final int clientPort = 9090;

    private final BeanResolver beanResolver;
    private Class<?> modelType = Object.class;
    private Map<String, Object> beans = new HashMap<String, Object>();
    private boolean serverStatusMessage;

    public SocketAppServiceBuilder() {
        beanResolver = new BeanResolver() {
            @Override
            public Object resolveByName(String beanName) {
                return beans.get(beanName);
            }
        };
    }

    public SocketAppServiceBuilder withModelType(Class<?> modelType) {
        this.modelType = modelType;
        return this;
    }

    public SocketAppServiceBuilder withBean(String beanName, Object bean) {
        this.beans.put(beanName, bean);
        return this;
    }

    public SocketAppServiceBuilder withServerStatusMessage(boolean value) {
        serverStatusMessage = value;
        return this;
    }

    public AppService create() {
        // createRefContext

        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                AnkorSystem serverSystem = SocketAnkorSystem.create("server", modelType, beanResolver, HOST, clientPort, serverPort)
                        .withRemoteMethodActionListenerEnabled();
                serverSystem.start();
                if (serverStatusMessage) {
                    startServerStatusThread(serverSystem);
                }
            }
        });
        serverThread.setDaemon(true);

        SocketAnkorSystem clientSystem = SocketAnkorSystem.create("client", modelType, null, HOST, serverPort, clientPort);

        // start
        serverThread.start();
        clientSystem.start();

        return new AppService(clientSystem);
    }

    public static void startServerStatusThread(final AnkorSystem system) {
        final long started = System.currentTimeMillis();
        new Thread(new Runnable() {
            public void run() {
                RefFactory refFactory = system.getRefContextFactory().createRefContext().refFactory();
                boolean interrupted = false;
                while (!interrupted) {
                    try {
                        Thread.sleep(1000 * 30);

                        long upSinceSeconds = (System.currentTimeMillis() - started) / 1000;
                        String serverStatus = String.format("server up time %ds", upSinceSeconds);

                        refFactory.rootRef().append("serverStatus").setValue(serverStatus);

                    } catch (InterruptedException e) {
                        interrupted = true;
                    }

                }
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}
