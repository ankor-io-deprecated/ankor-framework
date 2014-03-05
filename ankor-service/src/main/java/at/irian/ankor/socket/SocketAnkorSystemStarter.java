package at.irian.ankor.socket;

import at.irian.ankor.akka.AnkorActorSystem;
import at.irian.ankor.annotation.AnnotationBeanMetadataProvider;
import at.irian.ankor.application.Application;
import at.irian.ankor.base.BeanResolver;
import at.irian.ankor.delay.AkkaScheduler;
import at.irian.ankor.event.dispatch.AkkaEventDispatcherFactory;
import at.irian.ankor.event.dispatch.JavaFxEventDispatcherFactory;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefContextFactoryProvider;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.ref.el.ELRefContextFactoryProvider;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.session.SingletonModelSessionManager;
import at.irian.ankor.system.AnkorSystem;
import at.irian.ankor.system.AnkorSystemBuilder;
import at.irian.ankor.viewmodel.proxy.CglibProxyBeanFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Spiegl
 */
@SuppressWarnings("UnusedDeclaration")
@Deprecated
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

    private Application application;

    private RefContextFactoryProvider refContextFactoryProvider = new ELRefContextFactoryProvider();

    public SocketAnkorSystemStarter withApplication(Application application) {
        this.application = application;
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

    public SocketAnkorSystemStarter withRefContextFactoryProvider(RefContextFactoryProvider refContextFactoryProvider) {
        this.refContextFactoryProvider = refContextFactoryProvider;
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

        AnnotationBeanMetadataProvider beanMetadataProvider = new AnnotationBeanMetadataProvider();

        SocketMessageLoop.Host server = getServerLocalHost();

        AnkorActorSystem ankorActorSystem = AnkorActorSystem.create();
        AnkorSystem serverSystem = new AnkorSystemBuilder()
                .withName(server.getId())
                .withBeanResolver(beanResolver)
                .withApplication(application)
                .withDispatcherFactory(new AkkaEventDispatcherFactory(ankorActorSystem))
                .withScheduler(new AkkaScheduler(ankorActorSystem))
                .withRefContextFactoryProvider(refContextFactoryProvider)
                .withBeanMetadataProvider(beanMetadataProvider)
                .withBeanFactory(new CglibProxyBeanFactory(beanMetadataProvider))
                .createServer();

        serverSystem.start();
    }


    public RefFactory createAndStartClientSystem() {

        SocketMessageLoop.Host client = getClientLocalHost();

        AnkorSystemBuilder builder = new AnkorSystemBuilder().withName(client.getId());

        AnkorSystem clientSystem = builder
                .withDispatcherFactory(new JavaFxEventDispatcherFactory())
                .withRefContextFactoryProvider(refContextFactoryProvider)
                .createClient();

        // start
        clientSystem.start();

        ModelSession singletonModelSession = ((SingletonModelSessionManager) clientSystem.getModelSessionManager()).getModelSession();
        RefContext clientRefContext = singletonModelSession.getRefContext();
        return clientRefContext.refFactory();

    }

}
