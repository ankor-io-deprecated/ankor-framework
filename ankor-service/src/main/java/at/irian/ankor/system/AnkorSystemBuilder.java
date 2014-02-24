package at.irian.ankor.system;

import at.irian.ankor.annotation.AnnotationBeanMetadataProvider;
import at.irian.ankor.base.BeanResolver;
import at.irian.ankor.big.modify.ClientSideBigDataModifier;
import at.irian.ankor.big.modify.ServerSideBigDataModifier;
import at.irian.ankor.session.*;
import at.irian.ankor.delay.Scheduler;
import at.irian.ankor.delay.SimpleScheduler;
import at.irian.ankor.delay.TaskRequestEventListener;
import at.irian.ankor.event.ArrayListEventListeners;
import at.irian.ankor.event.EventListeners;
import at.irian.ankor.event.ModelEventListener;
import at.irian.ankor.event.dispatch.EventDispatcherFactory;
import at.irian.ankor.event.dispatch.SynchronisedEventDispatcherFactory;
import at.irian.ankor.messaging.*;
import at.irian.ankor.messaging.modify.CoerceTypeModifier;
import at.irian.ankor.messaging.modify.Modifier;
import at.irian.ankor.messaging.modify.PassThroughModifier;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefContextFactory;
import at.irian.ankor.ref.RefContextFactoryProvider;
import at.irian.ankor.ref.el.ELRefContextFactoryProvider;
import at.irian.ankor.connection.*;
import at.irian.ankor.viewmodel.ViewModelPostProcessor;
import at.irian.ankor.viewmodel.factory.BeanFactory;
import at.irian.ankor.viewmodel.factory.ReflectionBeanFactory;
import at.irian.ankor.viewmodel.listener.ActionListenersPostProcessor;
import at.irian.ankor.viewmodel.listener.ChangeListenersPostProcessor;
import at.irian.ankor.viewmodel.metadata.BeanMetadataProvider;
import at.irian.ankor.viewmodel.watch.WatchedViewModelPostProcessor;
import at.irian.ankor.websocket.AnkorClientEndpoint;
import at.irian.ankor.websocket.WebSocketMessageBus;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Manfred Geiler
 */
@SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
public class AnkorSystemBuilder {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorSystemBuilder.class);
    private static final long CONNECT_TIMEOUT = 10000;
    private static int modelSessionIdCnt = 0;
    private String systemName;
    private Config config;
    private List<ViewModelPostProcessor> viewModelPostProcessors;
    private MessageIdGenerator messageIdGenerator;
    private EventDispatcherFactory eventDispatcherFactory;
    private String modelSessionId;
    private Scheduler scheduler;
    private EventListeners defaultGlobalEventListeners;
    private ModelSessionFactory modelSessionFactory;
    private ModelRootFactory modelRootFactory;
    private BeanResolver beanResolver;
    private MessageBus messageBus;
    private List<ModelEventListener> customGlobalEventListeners;
    private RefContextFactoryProvider refContextFactoryProvider;
    private BeanMetadataProvider beanMetadataProvider;
    private BeanFactory beanFactory;

    public AnkorSystemBuilder() {
        this.systemName = null;
        this.config = ConfigFactory.load();
        this.viewModelPostProcessors = null;
        this.messageIdGenerator = null;
        this.eventDispatcherFactory = null;
        this.modelSessionId = null;
        this.scheduler = null;
        this.customGlobalEventListeners = new ArrayList<ModelEventListener>();
        this.modelSessionFactory = null;
        this.refContextFactoryProvider = new ELRefContextFactoryProvider();
        this.beanMetadataProvider = new AnnotationBeanMetadataProvider();
        this.beanFactory = null;
    }

    public AnkorSystemBuilder withName(String name) {
        this.systemName = name;
        return this;
    }

    public AnkorSystemBuilder withModelRootFactory(ModelRootFactory modelRootFactory) {
        this.modelRootFactory = modelRootFactory;
        return this;
    }

    public AnkorSystemBuilder withDefaultGlobalEventListeners(EventListeners defaultGlobalEventListeners) {
        this.defaultGlobalEventListeners = defaultGlobalEventListeners;
        return this;
    }

    public AnkorSystemBuilder withMessageBus(MessageBus messageBus) {
        this.messageBus = messageBus;
        return this;
    }

    public AnkorSystemBuilder withBeanResolver(BeanResolver beanResolver) {
        this.beanResolver = beanResolver;
        return this;
    }

    public AnkorSystemBuilder withDispatcherFactory(EventDispatcherFactory eventDispatcherFactory) {
        this.eventDispatcherFactory = eventDispatcherFactory;
        return this;
    }

    public AnkorSystemBuilder withScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
        return this;
    }

    public AnkorSystemBuilder withGlobalEventListener(ModelEventListener globalEventListener) {
        customGlobalEventListeners.add(globalEventListener);
        return this;
    }

    public AnkorSystemBuilder withGlobalEventListeners(List<ModelEventListener> globalEventListeners) {
        customGlobalEventListeners.addAll(globalEventListeners);
        return this;
    }

    public AnkorSystemBuilder withModelSessionId(String modelSessionId) {
        this.modelSessionId = modelSessionId;
        return this;
    }

    public AnkorSystemBuilder withRefContextFactoryProvider(RefContextFactoryProvider refContextFactoryProvider) {
        this.refContextFactoryProvider = refContextFactoryProvider;
        return this;
    }

    public AnkorSystemBuilder withBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        return this;
    }


    public AnkorSystem createServer() {

        String systemName = getServerSystemName();
        MessageBus messageBus = getMessageBus();
        EventDispatcherFactory eventDispatcherFactory = getEventDispatcherFactory();

        ModelRootFactory modelRootFactory = getServerModelRootFactory();

        BeanMetadataProvider beanMetadataProvider = getMetadataProvider();
        BeanFactory beanFactory = getBeanFactory();

        RefContextFactory refContextFactory =
                refContextFactoryProvider.createRefContextFactory(getServerBeanResolver(),
                                                                  getServerViewModelPostProcessors(),
                                                                  getScheduler(),
                                                                  modelRootFactory,
                                                                  beanMetadataProvider,
                                                                  beanFactory);

        MessageFactory messageFactory = getServerMessageFactory();

        ModelConnectionFactory modelConnectionFactory = new DefaultModelConnectionFactory(refContextFactory,
                                                                 eventDispatcherFactory,
                                                                 messageBus);
        ModelConnectionManager modelConnectionManager = new DefaultModelConnectionManager(modelConnectionFactory);

        Modifier defaultModifier = getDefaultModifier();
        Modifier bigDataModifier = new ServerSideBigDataModifier(defaultModifier);
        Modifier modifier = new CoerceTypeModifier(bigDataModifier);

        EventListeners globalEventListeners = getGlobalEventListeners(messageFactory,
                                                                      modelConnectionManager,
                                                                      modelRootFactory,
                                                                      modifier);

        ModelSessionFactory modelSessionFactory = getModelSessionFactory(eventDispatcherFactory, globalEventListeners);

        ModelSessionManager modelSessionManager = new DefaultModelSessionManager(modelSessionFactory);

        RemoteMessageListener remoteMessageListener = new DefaultRemoteMessageListener(modelSessionManager,
                                                                                       modelConnectionManager,
                                                                                       modelRootFactory,
                                                                                       modifier);
        return new AnkorSystem(systemName,
                               messageFactory,
                               messageBus,
                               refContextFactory,
                               modelSessionManager,
                               modelConnectionManager,
                               remoteMessageListener);
    }

    private BeanFactory getBeanFactory() {
        if (beanFactory == null) {
            beanFactory = new ReflectionBeanFactory(getMetadataProvider());
        }
        return beanFactory;
    }

    private BeanMetadataProvider getMetadataProvider() {
        return beanMetadataProvider;
    }

    private Modifier getDefaultModifier() {
        return new PassThroughModifier();
    }

    // TODO: Maybe refactor so a client can be crated via createClient() ?
    public AnkorSystem createWebSocketClient(String uri) throws IOException, DeploymentException, InterruptedException {
        WebSocketMessageBus messageBus = (WebSocketMessageBus) getMessageBus();

        CountDownLatch latch = new CountDownLatch(2);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(new AnkorClientEndpoint(this, messageBus, latch), URI.create(uri));

        if (latch.await(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)) {
            return createClient();
        }

        LOG.error("Could not connect to {} within {} milliseconds", uri, CONNECT_TIMEOUT);
        throw new RuntimeException("Could not connect within time");
    }

    public AnkorSystem createClient() {

        if (viewModelPostProcessors != null) {
            throw new IllegalStateException("viewModelPostProcessors not supported for client system");
        }

        String systemName = getClientSystemName();
        MessageBus messageBus = getMessageBus();

        ModelRootFactory modelRootFactory = getClientModelRootFactory();

        BeanMetadataProvider beanMetadataProvider = getMetadataProvider();
        BeanFactory beanFactory = getBeanFactory();

        RefContextFactory refContextFactory =
                refContextFactoryProvider.createRefContextFactory(getClientBeanResolver(),
                                                                  null,
                                                                  getScheduler(),
                                                                  modelRootFactory,
                                                                  beanMetadataProvider,
                                                                  beanFactory);

        MessageFactory messageFactory = getClientMessageFactory();

        String modelSessionId = getModelSessionId();

        SingletonModelConnectionManager connectionManager = new SingletonModelConnectionManager();
        Modifier defaultModifier = getDefaultModifier();
        Modifier modifier = new ClientSideBigDataModifier(defaultModifier);

        EventListeners globalEventListeners = getGlobalEventListeners(messageFactory,
                                                                      connectionManager,
                                                                      modelRootFactory,
                                                                      modifier);

        ModelSessionFactory modelSessionFactory = getModelSessionFactory(getEventDispatcherFactory(),
                                                                         globalEventListeners);

        ModelSession modelSession = modelSessionFactory.createModelSession(modelSessionId);

        RefContext refContext = refContextFactory.createRefContextFor(modelSession);

        RemoteSystem remoteSystem = getSingletonRemoteSystem(messageBus);

        MessageSender messageSender = messageBus.getMessageSenderFor(remoteSystem);

        connectionManager.setModelConnection(new SingletonModelConnection(modelSession, refContext, messageSender));

        ModelSessionManager modelSessionManager = new SingletonModelSessionManager(modelSessionId, modelSession);

        RemoteMessageListener remoteMessageListener = new DefaultRemoteMessageListener(modelSessionManager,
                                                                                       connectionManager,
                                                                                       modelRootFactory,
                                                                                       modifier);
        return new AnkorSystem(systemName,
                               messageFactory,
                               messageBus,
                               refContextFactory,
                               modelSessionManager,
                               connectionManager,
                               remoteMessageListener);
    }

    private EventListeners createDefaultGlobalEventListeners(MessageFactory messageFactory,
                                                             ModelConnectionManager modelConnectionManager,
                                                             ModelRootFactory modelRootFactory,
                                                             Modifier modifier) {

        EventListeners eventListeners = new ArrayListEventListeners();

        // action event listener for sending action events to remote partner
        eventListeners.add(new RemoteNotifyActionEventListener(messageFactory, modelConnectionManager, modifier));

        // global change event listener for sending change events to remote partner
        eventListeners.add(new RemoteNotifyChangeEventListener(messageFactory,
                                                               modelConnectionManager,
                                                               modelRootFactory,
                                                               modifier));

        // global change event listener for cleaning up obsolete model session listeners
        eventListeners.add(new ListenerCleanupChangeEventListener());

        // global task request event listener
        eventListeners.add(new TaskRequestEventListener());

        // global missing item action event listener
        eventListeners.add(new MissingPropertyActionEventListener());

        return eventListeners;
    }

    private List<ViewModelPostProcessor> createDefaultServerViewModelPostProcessors() {
        List<ViewModelPostProcessor> list = new ArrayList<ViewModelPostProcessor>();
        list.add(new ActionListenersPostProcessor());
        list.add(new ChangeListenersPostProcessor());
        list.add(new WatchedViewModelPostProcessor());
        return list;
    }

    private String getServerSystemName() {
        if (systemName == null) {
            systemName = "Unnamed Server";
            LOG.warn("No system name specified, using default name {}", systemName);
        }
        return systemName;
    }

    private String getClientSystemName() {
        if (systemName == null) {
            systemName = "Unnamed Client";
            LOG.warn("No system name specified, using default name {}", systemName);
        }
        return systemName;
    }

    private MessageIdGenerator getMessageIdGenerator(String systemName) {
        if (messageIdGenerator == null) {
            messageIdGenerator = createDefaultMessageIdGenerator(systemName);
        }
        return messageIdGenerator;
    }

    private MessageIdGenerator createDefaultMessageIdGenerator(String systemName) {
        return new CounterMessageIdGenerator(systemName + "#");
    }

    private ModelRootFactory getServerModelRootFactory() {
        if (modelRootFactory == null) {
            throw new IllegalStateException("modelRootFactory not set");
        }
        return modelRootFactory;
    }

    private ModelRootFactory getClientModelRootFactory() {
        if (modelRootFactory == null) {
            modelRootFactory = new ModelRootFactory() {
                @Override
                public Set<String> getKnownRootNames() {
                    return Collections.singleton("root");
                }

                @Override
                public Object createModelRoot(Ref rootRef) {
                    throw new UnsupportedOperationException();
                }
            };
        }
        return modelRootFactory;
    }

    private MessageBus getMessageBus() {
        if (messageBus == null) {
            throw new IllegalStateException("messageBus not set");
        }
        return messageBus;
    }

    private List<ViewModelPostProcessor> getServerViewModelPostProcessors() {
        if (viewModelPostProcessors == null) {
            viewModelPostProcessors = createDefaultServerViewModelPostProcessors();
        }
        return viewModelPostProcessors;
    }

    private BeanResolver getClientBeanResolver() {
        if (beanResolver != null) {
            throw new IllegalStateException("beanResolver not supported for client system");
        }
        return new EmptyBeanResolver();
    }

    private BeanResolver getServerBeanResolver() {
        if (beanResolver == null) {
            beanResolver = new EmptyBeanResolver();
        }
        return beanResolver;
    }

    private Scheduler getScheduler() {
        if (scheduler == null) {
            scheduler = new SimpleScheduler();
        }
        return scheduler;
    }

    private EventDispatcherFactory getEventDispatcherFactory() {
        if (eventDispatcherFactory == null) {
            eventDispatcherFactory = new SynchronisedEventDispatcherFactory();
        }
        return eventDispatcherFactory;
    }

    public EventListeners getGlobalEventListeners(MessageFactory messageFactory,
                                                  ModelConnectionManager modelConnectionManager,
                                                  ModelRootFactory modelRootFactory,
                                                  Modifier modifier) {
        EventListeners globalEventListeners;
        if (defaultGlobalEventListeners == null) {
            globalEventListeners = createDefaultGlobalEventListeners(messageFactory,
                                                                     modelConnectionManager,
                                                                     modelRootFactory,
                                                                     modifier);
        } else {
            globalEventListeners = defaultGlobalEventListeners;
        }

        for (ModelEventListener customGlobalEventListener : customGlobalEventListeners) {
            globalEventListeners.add(customGlobalEventListener);
        }

        return globalEventListeners;
    }

    private ModelSessionFactory getModelSessionFactory(EventDispatcherFactory eventDispatcherFactory,
                                                       EventListeners globalEventListeners) {
        if (modelSessionFactory == null) {
            modelSessionFactory = new DefaultModelSessionFactory(eventDispatcherFactory,
                                                                 globalEventListeners);
        }
        return modelSessionFactory;
    }

    private String getModelSessionId() {
        if (modelSessionId == null) {
            modelSessionId = "" + (++modelSessionIdCnt);
        }
        return modelSessionId;
    }

    private RemoteSystem getSingletonRemoteSystem(MessageBus messageBus) {
        Collection<? extends RemoteSystem> remoteSystems = messageBus.getKnownRemoteSystems();
        if (remoteSystems.size() != 1) {
            throw new IllegalStateException("None or multiple remote systems?");
        }
        return remoteSystems.iterator().next();
    }

    public MessageFactory getServerMessageFactory() {
        String systemName = getServerSystemName();
        return new MessageFactory(systemName, getMessageIdGenerator(systemName));
    }

    public MessageFactory getClientMessageFactory() {
        String systemName = getClientSystemName();
        return new MessageFactory(systemName, getMessageIdGenerator(systemName));
    }

    public BeanMetadataProvider getBeanMetadataProvider() {
        return beanMetadataProvider;
    }

    private static class EmptyBeanResolver implements BeanResolver {

        @Override
        public Object resolveByName(String beanName) {
            return null;
        }

        @Override
        public Collection<String> getKnownBeanNames() {
            return Collections.emptyList();
        }
    }

}
