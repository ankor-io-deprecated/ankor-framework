package at.irian.ankor.fx.app;

import at.irian.ankor.base.BeanResolver;
import at.irian.ankor.event.dispatch.AkkaEventDispatcherFactory;
import at.irian.ankor.messaging.MessageMapper;
import at.irian.ankor.messaging.SocketMessageLoop;
import at.irian.ankor.messaging.json.simpletree.SimpleTreeJsonMessageMapper;
import at.irian.ankor.messaging.json.viewmodel.ViewModelJsonMessageMapper;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.session.ModelRootFactory;
import at.irian.ankor.session.SingletonSessionManager;
import at.irian.ankor.system.AnkorSystem;
import at.irian.ankor.system.AnkorSystemBuilder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Spiegl
 */
@SuppressWarnings("UnusedDeclaration")
public class SocketAppBuilder {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketAppBuilder.class);

    private static final String HOST = "localhost";
    private static final int serverPort = 8080;
    private static final int clientPort = 9090;

    private final BeanResolver beanResolver;
    private Map<String, Object> beans = new HashMap<>();
    private ModelRootFactory modelRootFactory;

    public SocketAppBuilder() {
        beanResolver = new BeanResolver() {
            @Override
            public Object resolveByName(String beanName) {
                return beans.get(beanName);
            }

            @Override
            public Collection<String> getKnownBeanNames() {
                return beans.keySet();
            }
        };
    }

    public SocketAppBuilder withModelRootFactory(ModelRootFactory modelRootFactory) {
        this.modelRootFactory = modelRootFactory;
        return this;
    }

    public SocketAppBuilder withBean(String beanName, Object bean) {
        this.beans.put(beanName, bean);
        return this;
    }

    /**
     * @return the client's RefFactory
     */
    public RefFactory create() {

        String serverName = "server";

        MessageMapper<String> serverMessageMapper = new ViewModelJsonMessageMapper();

        SocketMessageLoop<String> serverMessageLoop = new SocketMessageLoop<>(serverName,
                                                                              serverMessageMapper,
                                                                              HOST,
                                                                              clientPort,
                                                                              serverPort);

        AkkaEventDispatcherFactory akkaDispatcherFactory = new AkkaEventDispatcherFactory();

        AnkorSystem serverSystem = new AnkorSystemBuilder()
                .withName(serverName)
                .withBeanResolver(beanResolver)
                .withModelRootFactory(modelRootFactory)
                .withMessageBus(serverMessageLoop.getMessageBus())
                .withDispatcherFactory(akkaDispatcherFactory)
                .createServer();

        serverSystem.start();
        serverMessageLoop.start(true);



        String clientName = "client";

        MessageMapper<String> clientMessageMapper = new SimpleTreeJsonMessageMapper();

        SocketMessageLoop<String> clientMessageLoop = new SocketMessageLoop<>(clientName,
                                                                              clientMessageMapper,
                                                                              HOST,
                                                                              serverPort,
                                                                              clientPort);
        AnkorSystem clientSystem = new AnkorSystemBuilder()
                .withName(clientName)
                .withMessageBus(clientMessageLoop.getMessageBus())
                .createClient();

        // start
        clientSystem.start();
        clientMessageLoop.start(true);

        RefContext clientRefContext = ((SingletonSessionManager)clientSystem.getSessionManager()).getSession().getRefContext();
        return clientRefContext.refFactory();
    }

}
