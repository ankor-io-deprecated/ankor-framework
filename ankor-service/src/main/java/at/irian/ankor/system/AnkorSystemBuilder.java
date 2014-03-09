package at.irian.ankor.system;

import at.irian.ankor.annotation.AnnotationBeanMetadataProvider;
import at.irian.ankor.application.*;
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
import at.irian.ankor.ref.RefContextFactory;
import at.irian.ankor.ref.RefContextFactoryProvider;
import at.irian.ankor.ref.el.ELRefContextFactoryProvider;
import at.irian.ankor.session.*;
import at.irian.ankor.switching.SimpleSwitchboardFactory;
import at.irian.ankor.switching.Switchboard;
import at.irian.ankor.switching.SwitchboardFactory;
import at.irian.ankor.switching.connector.local.ModelSessionOpenHandler;
import at.irian.ankor.switching.handler.OpenHandler;
import at.irian.ankor.switching.routing.DefaultRoutingTable;
import at.irian.ankor.switching.routing.RoutingTable;
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
    private SwitchboardFactory switchboardFactory;
    private RefContextFactoryProvider refContextFactoryProvider;
    private BeanMetadataProvider beanMetadataProvider;
    private BeanFactory beanFactory;
    private RoutingTable routingTable;
    private OpenHandler openHandler;

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
        this.routingTable = null;
        this.openHandler = null;
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
    public AnkorSystemBuilder withMessageBusFactory(SwitchboardFactory switchboardFactory) {
        this.switchboardFactory = switchboardFactory;
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

    public AnkorSystemBuilder withRoutingTable(RoutingTable routingTable) {
        this.routingTable = routingTable;
        return this;
    }

    public AnkorSystemBuilder withOpenHandler(OpenHandler openHandler) {
        this.openHandler = openHandler;
        return this;
    }


    public AnkorSystem createServer() {

        Switchboard switchboard = createSwitchboard();

        EventDispatcherFactory eventDispatcherFactory = getEventDispatcherFactory();

        Application application = getServerApplication();

        BeanMetadataProvider beanMetadataProvider = getBeanMetadataProvider();
        BeanFactory beanFactory = getBeanFactory();

        RefContextFactory refContextFactory =
                refContextFactoryProvider.createRefContextFactory(getServerBeanResolver(),
                                                                  getServerViewModelPostProcessors(),
                                                                  getScheduler(),
                                                                  beanMetadataProvider,
                                                                  beanFactory,
                                                                  switchboard);

        Modifier defaultModifier = getDefaultModifier();
        Modifier bigDataModifier = new ServerSideBigDataModifier(defaultModifier);
        Modifier modifier = new CoerceTypeModifier(bigDataModifier);

        EventListeners defaultEventListeners = createDefaultEventListeners(switchboard, modifier);

        ModelSessionFactory modelSessionFactory = getModelSessionFactory(eventDispatcherFactory,
                                                                         defaultEventListeners,
                                                                         refContextFactory);

        ModelSessionManager modelSessionManager = new DefaultModelSessionManager();

        RoutingTable routingTable = getRoutingTable();

        if (!configValues.containsKey(MESSAGE_MAPPER_CONFIG_KEY)) {
            configValues.put(MESSAGE_MAPPER_CONFIG_KEY, ViewModelJsonMessageMapper.class.getName());
        }

        switchboard.registerOpenHandler(getServerOpenHandler(modelSessionManager, application));

        return new AnkorSystem(application,
                               getConfig(),
                               switchboard,
                               refContextFactory,
                               modelSessionManager,
                               modelSessionFactory, routingTable,
                               modifier,
                               beanMetadataProvider);
    }

    public AnkorSystem createClient() {

        if (viewModelPostProcessors != null) {
            throw new IllegalStateException("viewModelPostProcessors not supported for client system");
        }

        Switchboard switchboard = createSwitchboard();

        Application application = getClientApplication();

        BeanMetadataProvider beanMetadataProvider = getBeanMetadataProvider();
        BeanFactory beanFactory = getBeanFactory();

        RefContextFactory refContextFactory =
                refContextFactoryProvider.createRefContextFactory(getClientBeanResolver(),
                                                                  null,
                                                                  getScheduler(),
                                                                  beanMetadataProvider,
                                                                  beanFactory,
                                                                  switchboard);

        Modifier defaultModifier = getDefaultModifier();
        Modifier modifier = new ClientSideBigDataModifier(defaultModifier);

        EventListeners defaultEventListeners = createDefaultEventListeners(switchboard, modifier);

        ModelSessionFactory modelSessionFactory = getModelSessionFactory(getEventDispatcherFactory(),
                                                                         defaultEventListeners, refContextFactory);

        ModelSession modelSession = modelSessionFactory.createModelSession();

        ModelSessionManager modelSessionManager = new SingletonModelSessionManager();
        modelSessionManager.add(modelSession);

        RoutingTable routingTable = getRoutingTable();

        if (!configValues.containsKey(MESSAGE_MAPPER_CONFIG_KEY)) {
            configValues.put(MESSAGE_MAPPER_CONFIG_KEY, SimpleTreeJsonMessageMapper.class.getName());
        }

        switchboard.registerOpenHandler(getClientOpenHandler());

        return new AnkorSystem(application,
                               getConfig(),
                               switchboard,
                               refContextFactory,
                               modelSessionManager,
                               modelSessionFactory, routingTable,
                               modifier,
                               beanMetadataProvider);
    }

    private RoutingTable getRoutingTable() {
        if (routingTable == null) {
            routingTable = new DefaultRoutingTable();
        }
        return routingTable;
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

    private EventListeners createDefaultEventListeners(Switchboard switchboard, Modifier modifier) {

        EventListeners eventListeners = new ArrayListEventListeners();

        // action event listener for sending action events to remote partner
        eventListeners.add(new RemoteNotifyActionEventListener(switchboard, modifier));

        // global change event listener for sending change events to remote partner
        eventListeners.add(new RemoteNotifyChangeEventListener(switchboard, modifier));

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
            application = new SimpleClientApplication(getClientSystemName());
        }
        return application;
    }

    private Switchboard createSwitchboard() {
        if (switchboardFactory == null) {
            switchboardFactory = new SimpleSwitchboardFactory();
        }
        return switchboardFactory.createSwitchboard();
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
                                                                 refContextFactory, application);
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

    private OpenHandler getServerOpenHandler(ModelSessionManager modelSessionManager, Application application) {
        if (openHandler == null) {
            openHandler = new ModelSessionOpenHandler(modelSessionFactory, modelSessionManager, application);
        }
        return openHandler;
    }

    private OpenHandler getClientOpenHandler() {
        if (openHandler == null) {
            throw new IllegalStateException("No OpenHandler declared - typical open handlers for clients are called 'Fixed...OpenHandler'");
        }
        return openHandler;
    }
}
