package at.irian.ankor.fx.app;

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

    public SocketAppServiceBuilder() {
        beanResolver = new BeanResolver() {
            @Override
            public Object resolveByName(String beanName) {
                return beans.get(beanName);
            }

            @Override
            public String[] getBeanDefinitionNames() {
                return beans.keySet().toArray(new String[beans.keySet().size()]);
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

    public AppService create() {
        // createRefContext

        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                AnkorSystem serverSystem = SocketAnkorSystem
                        .create("server", modelType, beanResolver, HOST, clientPort, serverPort, true);
                serverSystem.start();
            }
        });
        serverThread.setDaemon(true);

        SocketAnkorSystem clientSystem = SocketAnkorSystem.create("client", modelType, null, HOST, serverPort, clientPort, false);

        // start
        serverThread.start();
        clientSystem.start();

        return new AppService(clientSystem);
    }

}
