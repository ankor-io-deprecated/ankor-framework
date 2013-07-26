package at.irian.ankor.fx.app;

import at.irian.ankor.messaging.MessageMapper;
import at.irian.ankor.messaging.SocketMessageLoop;
import at.irian.ankor.messaging.json.JsonViewDataMessageMapper;
import at.irian.ankor.messaging.json.JsonViewModelMessageMapper;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.session.ModelRootFactory;
import at.irian.ankor.system.AnkorSystem;
import at.irian.ankor.system.AnkorSystemBuilder;
import at.irian.ankor.system.BeanResolver;

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
    private Map<String, Object> beans = new HashMap<String, Object>();
    private ModelRootFactory modelRootFactory;

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

    public SocketAppServiceBuilder withModelRootFactory(ModelRootFactory modelRootFactory) {
        this.modelRootFactory = modelRootFactory;
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

                String serverName = "server";

                MessageMapper<String> serverMessageMapper = new JsonViewModelMessageMapper();

                SocketMessageLoop<String> serverMessageLoop = new SocketMessageLoop<String>(serverName,
                                                                                            serverMessageMapper,
                                                                                      HOST,
                                                                                      clientPort,
                                                                                      serverPort);
                AnkorSystem serverSystem = new AnkorSystemBuilder()
                        .withName(serverName)
                        .withBeanResolver(beanResolver)
                        .withModelRootFactory(modelRootFactory)
                        .withMessageBus(serverMessageLoop.getMessageBus())
                        .createServer();

                serverSystem.start();
                serverMessageLoop.start();

            }
        });
        serverThread.setDaemon(true);


        String clientName = "client";

        MessageMapper<String> clientMessageMapper = new JsonViewDataMessageMapper();

        SocketMessageLoop<String> clientMessageLoop = new SocketMessageLoop<String>(clientName,
                                                                                    clientMessageMapper,
                                                                              HOST,
                                                                              serverPort,
                                                                              clientPort);
        AnkorSystem clientSystem = new AnkorSystemBuilder()
                .withName(clientName)
                .withMessageBus(clientMessageLoop.getMessageBus())
                .createClient();

        // start
        serverThread.start();
        clientSystem.start();

        clientMessageLoop.start();

        RefContext clientRefContext = clientSystem.getSessionManager().getOrCreateSession(null).getRefContext();
        RefFactory clientRefFactory = clientRefContext.refFactory();
        return new AppService(clientRefFactory);
    }

}
