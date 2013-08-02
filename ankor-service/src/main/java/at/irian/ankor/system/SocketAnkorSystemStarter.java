package at.irian.ankor.system;

import at.irian.ankor.akka.AnkorActorSystem;
import at.irian.ankor.base.BeanResolver;
import at.irian.ankor.delay.AkkaScheduler;
import at.irian.ankor.event.dispatch.AkkaEventDispatcherFactory;
import at.irian.ankor.messaging.json.simpletree.SimpleTreeJsonMessageMapper;
import at.irian.ankor.messaging.json.viewmodel.ViewModelJsonMessageMapper;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.session.ModelRootFactory;
import at.irian.ankor.session.SingletonSessionManager;
import at.irian.ankor.socket.ClientSocketMessageLoop;
import at.irian.ankor.socket.SocketMessageLoop;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Spiegl
 */
@SuppressWarnings("UnusedDeclaration")
public class SocketAnkorSystemStarter {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketAnkorSystemStarter.class);

    private static final String LOCALHOST = "localhost";
    private static final int DEFAULT_SERVER_PORT = 8080;

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

    private SocketMessageLoop.Host server = new SocketMessageLoop.Host("server", LOCALHOST, DEFAULT_SERVER_PORT);
    private final Map<String, SocketMessageLoop.Host> clients = new HashMap<String, SocketMessageLoop.Host>();

    private ModelRootFactory modelRootFactory;

    public SocketAnkorSystemStarter withModelRootFactory(ModelRootFactory modelRootFactory) {
        this.modelRootFactory = modelRootFactory;
        return this;
    }

    public SocketAnkorSystemStarter withBean(String beanName, Object bean) {
        this.beans.put(beanName, bean);
        return this;
    }

    public SocketAnkorSystemStarter withLocalServer(String serverName, int serverPort) {
        this.server = new SocketMessageLoop.Host(serverName, LOCALHOST, serverPort);
        return this;
    }

    public SocketAnkorSystemStarter withLocalClient(String clientName, int clientPort) {
        this.clients.put(clientName, new SocketMessageLoop.Host(clientName, LOCALHOST, clientPort));
        return this;
    }


    public void createAndStartServerSystem() {
        SocketMessageLoop<String> serverMessageLoop = new SocketMessageLoop<String>(server, new ViewModelJsonMessageMapper());
        for (SocketMessageLoop.Host client : clients.values()) {
            serverMessageLoop.addRemoteSystem(client);
        }

        AnkorActorSystem ankorActorSystem = AnkorActorSystem.create();
        AnkorSystem serverSystem = new AnkorSystemBuilder()
                .withName(server.getId())
                .withBeanResolver(beanResolver)
                .withModelRootFactory(modelRootFactory)
                .withMessageBus(serverMessageLoop.getMessageBus())
//              .withDispatcherFactory(new SynchronisedEventDispatcherFactory())
                .withDispatcherFactory(new AkkaEventDispatcherFactory(ankorActorSystem))
                .withScheduler(new AkkaScheduler(ankorActorSystem))
                .createServer();

        serverSystem.start();
        serverMessageLoop.start(true);
    }


    public RefFactory createAndStartClientSystem(String clientName) {

        SocketMessageLoop.Host client = clients.get(clientName);
        if (client == null) {
            throw new IllegalArgumentException("Unknown client");
        }

        AnkorSystemBuilder builder = new AnkorSystemBuilder().withName(client.getId());
        SocketMessageLoop<String> clientMessageLoop = new ClientSocketMessageLoop<String>(client, new SimpleTreeJsonMessageMapper(),
                                                                                     server,
                                                                                     builder.getClientMessageFactory());

        AnkorSystem clientSystem = builder
                .withMessageBus(clientMessageLoop.getMessageBus())
                .createClient();

        // start
        clientSystem.start();
        clientMessageLoop.start(true);

        RefContext clientRefContext = ((SingletonSessionManager)clientSystem.getSessionManager()).getSession().getRefContext();
        return clientRefContext.refFactory();

    }

}
