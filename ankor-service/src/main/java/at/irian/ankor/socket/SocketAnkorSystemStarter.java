package at.irian.ankor.socket;

import at.irian.ankor.akka.AnkorActorSystem;
import at.irian.ankor.base.BeanResolver;
import at.irian.ankor.delay.AkkaScheduler;
import at.irian.ankor.event.ModelEventListener;
import at.irian.ankor.event.dispatch.AkkaEventDispatcherFactory;
import at.irian.ankor.event.dispatch.JavaFxEventDispatcherFactory;
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
public class SocketAnkorSystemStarter {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketAnkorSystemStarter.class);

    private static final SocketMessageLoop.Host DEFAULT_SERVER_HOST = new SocketMessageLoop.Host("server", "localhost", 8080);
    private static final SocketMessageLoop.Host DEFAULT_CLIENT_HOST = new SocketMessageLoop.Host("client", "localhost", 9090);

    private final BeanResolver beanResolver = new BeanResolver() {
        @Override
        public Object resolveByName(String beanName) {
            return beans.get(beanName);
        }

        @Override
        public Collection<String> getKnownBeanNames() {
            return beans.keySet();
        }
    };
    private final Map<String, Object> beans = new HashMap<String, Object>();

    private SocketMessageLoop.Host localHost;
    private SocketMessageLoop.Host serverHost;

    private ModelRootFactory modelRootFactory;

    private ModelEventListener globalEventListener;

    public SocketAnkorSystemStarter withModelRootFactory(ModelRootFactory modelRootFactory) {
        this.modelRootFactory = modelRootFactory;
        return this;
    }

    public SocketAnkorSystemStarter withBean(String beanName, Object bean) {
        this.beans.put(beanName, bean);
        return this;
    }

    public SocketAnkorSystemStarter withLocalHost(SocketMessageLoop.Host host) {
        this.localHost = host;
        return this;
    }

    public SocketAnkorSystemStarter withServerHost(SocketMessageLoop.Host host) {
        this.serverHost = host;
        return this;
    }

    public SocketAnkorSystemStarter withGlobalEventListener(ModelEventListener globalEventListener) {
        this.globalEventListener = globalEventListener;
        return this;
    }

    public SocketMessageLoop.Host getServerLocalHost() {
        if (localHost == null) {
            localHost = DEFAULT_SERVER_HOST;
        }
        return localHost;
    }

    public SocketMessageLoop.Host getClientLocalHost() {
        if (localHost == null) {
            localHost = DEFAULT_CLIENT_HOST;
        }
        return localHost;
    }

    public SocketMessageLoop.Host getServerHost() {
        if (serverHost == null) {
            serverHost = DEFAULT_SERVER_HOST;
        }
        return serverHost;
    }

    public void createAndStartServerSystem(boolean daemon) {

        SocketMessageLoop.Host server = getServerLocalHost();
        SocketMessageLoop<String> serverMessageLoop = new ServerSocketMessageLoop<String>(server, new ViewModelJsonMessageMapper());

        AnkorActorSystem ankorActorSystem = AnkorActorSystem.create();
        AnkorSystem serverSystem = new AnkorSystemBuilder()
                .withName(server.getId())
                .withBeanResolver(beanResolver)
                .withModelRootFactory(modelRootFactory)
                .withMessageBus(serverMessageLoop.getMessageBus())
//              .withDispatcherFactory(new SynchronisedEventDispatcherFactory())
                .withDispatcherFactory(new AkkaEventDispatcherFactory(ankorActorSystem))
                .withGlobalEventListener(globalEventListener)
                .withScheduler(new AkkaScheduler(ankorActorSystem))
                .createServer();

        serverSystem.start();
        serverMessageLoop.start(daemon);
    }


    public RefFactory createAndStartClientSystem() {

        SocketMessageLoop.Host client = getClientLocalHost();

        AnkorSystemBuilder builder = new AnkorSystemBuilder().withName(client.getId());
        SocketMessageLoop<String> clientMessageLoop = new ClientSocketMessageLoop<String>(client, new SimpleTreeJsonMessageMapper(),
                                                                                     getServerHost(),
                                                                                     builder.getClientMessageFactory());
        builder.withGlobalEventListener(globalEventListener);

        AnkorSystem clientSystem = builder
                .withMessageBus(clientMessageLoop.getMessageBus())
                .withDispatcherFactory(new JavaFxEventDispatcherFactory())
                .createClient();

        // start
        clientSystem.start();
        clientMessageLoop.start(true);

        RefContext clientRefContext = ((SingletonSessionManager)clientSystem.getSessionManager()).getSession().getRefContext();
        return clientRefContext.refFactory();

    }

}
