package at.irian.ankor.system;

import at.irian.ankor.annotation.AnnotationBeanMetadataProvider;
import at.irian.ankor.application.Application;
import at.irian.ankor.application.ApplicationInstance;
import at.irian.ankor.application.SimpleApplicationInstance;
import at.irian.ankor.base.BeanResolver;
import at.irian.ankor.big.modify.ClientSideBigDataModifier;
import at.irian.ankor.big.modify.ServerSideBigDataModifier;
import at.irian.ankor.delay.Scheduler;
import at.irian.ankor.delay.SimpleScheduler;
import at.irian.ankor.delay.TaskRequestEventListener;
import at.irian.ankor.event.ArrayListEventListeners;
import at.irian.ankor.event.EventListeners;
import at.irian.ankor.event.dispatch.EventDispatcherFactory;
import at.irian.ankor.event.dispatch.SynchronisedEventDispatcherFactory;
import at.irian.ankor.messaging.json.simpletree.SimpleTreeJsonMessageMapper;
import at.irian.ankor.messaging.json.viewmodel.ViewModelJsonMessageMapper;
import at.irian.ankor.messaging.modify.CoerceTypeModifier;
import at.irian.ankor.messaging.modify.Modifier;
import at.irian.ankor.messaging.modify.PassThroughModifier;
import at.irian.ankor.msg.*;
import at.irian.ankor.ref.RefContextFactory;
import at.irian.ankor.ref.RefContextFactoryProvider;
import at.irian.ankor.ref.el.ELRefContextFactoryProvider;
import at.irian.ankor.session.*;
import at.irian.ankor.viewmodel.ViewModelPostProcessor;
import at.irian.ankor.viewmodel.factory.BeanFactory;
import at.irian.ankor.viewmodel.factory.ReflectionBeanFactory;
import at.irian.ankor.viewmodel.listener.ActionListenersPostProcessor;
import at.irian.ankor.viewmodel.listener.ChangeListenersPostProcessor;
import at.irian.ankor.viewmodel.metadata.BeanMetadataProvider;
import at.irian.ankor.viewmodel.watch.WatchedViewModelPostProcessor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.*;

/**
 * @author Manfred Geiler
 */
public class AnkorSystemBuilder {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorSystemBuilder.class);

    private static final String MESSAGE_MAPPER_CONFIG_KEY = "at.irian.ankor.messaging.MessageMapper";

    private String systemName;
    private Map<String, Object> configValues;
    private List<ViewModelPostProcessor> viewModelPostProcessors;
    private EventDispatcherFactory eventDispatcherFactory;
    private Scheduler scheduler;
    private ModelSessionFactory modelSessionFactory;
    private Application application;
    private BeanResolver beanResolver;
    private MessageBusFactory messageBusFactory;
    private RefContextFactoryProvider refContextFactoryProvider;
    private BeanMetadataProvider beanMetadataProvider;
    private BeanFactory beanFactory;
    private boolean socketConnector;

    public AnkorSystemBuilder() {
        this.systemName = null;
        this.viewModelPostProcessors = null;
        this.eventDispatcherFactory = null;
        this.scheduler = null;
        this.modelSessionFactory = null;
        this.refContextFactoryProvider = new ELRefContextFactoryProvider();
        this.beanMetadataProvider = null;
        this.beanFactory = null;
        this.configValues = new HashMap<String, Object>();
    }

    public AnkorSystemBuilder withName(String name) {
        this.systemName = name;
        return this;
    }

    public AnkorSystemBuilder withApplication(Application application) {
        this.application = application;
        return this;
    }

    public AnkorSystemBuilder withConfigValue(String key, Object value) {
        this.configValues.put(key, value);
        return this;
    }

    @SuppressWarnings("UnusedDeclaration")
    public AnkorSystemBuilder withMessageBusFactory(MessageBusFactory messageBusFactory) {
        this.messageBusFactory = messageBusFactory;
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

    public AnkorSystemBuilder withRefContextFactoryProvider(RefContextFactoryProvider refContextFactoryProvider) {
        this.refContextFactoryProvider = refContextFactoryProvider;
        return this;
    }

    public AnkorSystemBuilder withBeanMetadataProvider(BeanMetadataProvider beanMetadataProvider) {
        this.beanMetadataProvider = beanMetadataProvider;
        return this;
    }


    public AnkorSystemBuilder withBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        return this;
    }


    public AnkorSystem createServer() {

        MessageBus messageBus = createMessageBus();
        EventDispatcherFactory eventDispatcherFactory = getEventDispatcherFactory();

        Application application = getServerApplication();

        BeanMetadataProvider beanMetadataProvider = getBeanMetadataProvider();
        BeanFactory beanFactory = getBeanFactory();

        RefContextFactory refContextFactory =
                refContextFactoryProvider.createRefContextFactory(getServerBeanResolver(),
                                                                  getServerViewModelPostProcessors(),
                                                                  getScheduler(),
                                                                  beanMetadataProvider,
                                                                  beanFactory);

        Modifier defaultModifier = getDefaultModifier();
        Modifier bigDataModifier = new ServerSideBigDataModifier(defaultModifier);
        Modifier modifier = new CoerceTypeModifier(bigDataModifier);

        EventListeners defaultEventListeners = createDefaultEventListeners(messageBus, modifier);

        ModelSessionFactory modelSessionFactory = getModelSessionFactory(eventDispatcherFactory,
                                                                         defaultEventListeners,
                                                                         refContextFactory);

        ModelSessionManager modelSessionManager = new DefaultModelSessionManager(modelSessionFactory);

        SwitchingCenter switchingCenter = new SwitchingCenter();

        List<MessageListener> defaultMessageListeners = createDefaultMessageListeners(switchingCenter, messageBus);

        if (!configValues.containsKey(MESSAGE_MAPPER_CONFIG_KEY)) {
            configValues.put(MESSAGE_MAPPER_CONFIG_KEY, ViewModelJsonMessageMapper.class.getName());
        }

        return new AnkorSystem(application,
                               getConfig(),
                               messageBus,
                               refContextFactory,
                               modelSessionManager,
                               switchingCenter,
                               modifier,
                               defaultMessageListeners,
                               beanMetadataProvider);
    }

    public AnkorSystem createClient() {

        if (viewModelPostProcessors != null) {
            throw new IllegalStateException("viewModelPostProcessors not supported for client system");
        }

        MessageBus messageBus = createMessageBus();

        Application application = getClientApplication();
        ApplicationInstance singletonApplicationInstance = application.getApplicationInstance(null);

        BeanMetadataProvider beanMetadataProvider = getBeanMetadataProvider();
        BeanFactory beanFactory = getBeanFactory();

        RefContextFactory refContextFactory =
                refContextFactoryProvider.createRefContextFactory(getClientBeanResolver(),
                                                                  null,
                                                                  getScheduler(),
                                                                  beanMetadataProvider,
                                                                  beanFactory);

        Modifier defaultModifier = getDefaultModifier();
        Modifier modifier = new ClientSideBigDataModifier(defaultModifier);

        EventListeners defaultEventListeners = createDefaultEventListeners(messageBus, modifier);

        ModelSessionFactory modelSessionFactory = getModelSessionFactory(getEventDispatcherFactory(),
                                                                         defaultEventListeners, refContextFactory);

        ModelSession modelSession = modelSessionFactory.createModelSession(singletonApplicationInstance);

        ModelSessionManager modelSessionManager = new SingletonModelSessionManager(singletonApplicationInstance,
                                                                                   modelSession);

        SwitchingCenter switchingCenter = new SwitchingCenter();

        List<MessageListener> defaultMessageListeners = createDefaultMessageListeners(switchingCenter, messageBus);

        if (!configValues.containsKey(MESSAGE_MAPPER_CONFIG_KEY)) {
            configValues.put(MESSAGE_MAPPER_CONFIG_KEY, SimpleTreeJsonMessageMapper.class.getName());
        }

        return new AnkorSystem(application,
                               getConfig(),
                               messageBus,
                               refContextFactory,
                               modelSessionManager,
                               switchingCenter,
                               modifier,
                               defaultMessageListeners,
                               beanMetadataProvider);
    }

    private List<MessageListener> createDefaultMessageListeners(SwitchingCenter switchingCenter, MessageBus messageBus) {
        List<MessageListener> messageListeners = new ArrayList<MessageListener>();
        messageListeners.add(new DefaultDisconnectMessageListener(switchingCenter, messageBus));
        return messageListeners;
    }

    private BeanFactory getBeanFactory() {
        if (beanFactory == null) {
            beanFactory = new ReflectionBeanFactory(getBeanMetadataProvider());
        }
        return beanFactory;
    }

    private BeanMetadataProvider getBeanMetadataProvider() {
        if (beanMetadataProvider == null) {
            beanMetadataProvider = new AnnotationBeanMetadataProvider();
        }
        return beanMetadataProvider;
    }

    private Modifier getDefaultModifier() {
        return new PassThroughModifier();
    }

    private EventListeners createDefaultEventListeners(MessageBus messageBus, Modifier modifier) {

        EventListeners eventListeners = new ArrayListEventListeners();

        // action event listener for sending action events to remote partner
        eventListeners.add(new RemoteNotifyActionEventListener(messageBus, modifier));

        // global change event listener for sending change events to remote partner
        eventListeners.add(new RemoteNotifyChangeEventListener(messageBus, modifier));

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

    private String getClientSystemName() {
        if (systemName == null) {
            systemName = "Unnamed Client";
            LOG.warn("No system name specified, using default name {}", systemName);
        }
        return systemName;
    }

    private Application getServerApplication() {
        if (application == null) {
            throw new IllegalStateException("application not set");
        }
        return application;
    }

    private Application getClientApplication() {
        if (application == null) {
            final ApplicationInstance singleton = new SimpleApplicationInstance();
            application = new Application() {

                @Override
                public String getName() {
                    return getClientSystemName();
                }

                @Override
                public ApplicationInstance getApplicationInstance(Map<String, Object> connectParameters) {
                    return singleton;
                }

            };
        }
        return application;
    }

    private MessageBus createMessageBus() {
        if (messageBusFactory == null) {
            messageBusFactory = new SimpleMessageBusFactory();
        }
        return messageBusFactory.createMessageBus();
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

    private Config getConfig() {
        return ConfigFactory.parseMap(configValues, "AnkorSystemBuilder")
                .withFallback(ConfigFactory.load());
    }

    private EventDispatcherFactory getEventDispatcherFactory() {
        if (eventDispatcherFactory == null) {
            eventDispatcherFactory = new SynchronisedEventDispatcherFactory();
        }
        return eventDispatcherFactory;
    }

    private ModelSessionFactory getModelSessionFactory(EventDispatcherFactory eventDispatcherFactory,
                                                       EventListeners defaultEventListeners,
                                                       RefContextFactory refContextFactory) {
        if (modelSessionFactory == null) {
            modelSessionFactory = new DefaultModelSessionFactory(eventDispatcherFactory,
                                                                 defaultEventListeners,
                                                                 refContextFactory);
        }
        return modelSessionFactory;
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
