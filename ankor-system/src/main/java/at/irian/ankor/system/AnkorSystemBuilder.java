/*
 * Copyright (C) 2013-2014  Irian Solutions  (http://www.irian.at)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.irian.ankor.system;

import akka.actor.ActorSystem;
import at.irian.ankor.annotation.AnnotationBeanMetadataProvider;
import at.irian.ankor.application.Application;
import at.irian.ankor.application.SimpleClientApplication;
import at.irian.ankor.base.BeanResolver;
import at.irian.ankor.big.modify.ClientSideBigDataModifier;
import at.irian.ankor.big.modify.ServerSideBigDataModifier;
import at.irian.ankor.console.ConsoleApplication;
import at.irian.ankor.delay.AkkaScheduler;
import at.irian.ankor.delay.Scheduler;
import at.irian.ankor.delay.SimpleScheduler;
import at.irian.ankor.delay.TaskRequestEventListener;
import at.irian.ankor.event.*;
import at.irian.ankor.event.dispatch.AkkaSessionBoundEventDispatcherFactory;
import at.irian.ankor.event.dispatch.EventDispatcherFactory;
import at.irian.ankor.event.dispatch.SynchronizedSimpleEventDispatcherFactory;
import at.irian.ankor.monitor.AnkorSystemMonitor;
import at.irian.ankor.monitor.akka.AkkaAnkorSystemMonitor;
import at.irian.ankor.monitor.stats.AnkorSystemStats;
import at.irian.ankor.monitor.stats.StatsAnkorSystemMonitor;
import at.irian.ankor.ref.RefContextFactory;
import at.irian.ankor.ref.RefContextFactoryProvider;
import at.irian.ankor.ref.el.ELRefContextFactoryProvider;
import at.irian.ankor.serialization.json.simpletree.SimpleTreeJsonMessageMapper;
import at.irian.ankor.serialization.json.viewmodel.ViewModelJsonMessageMapper;
import at.irian.ankor.serialization.modify.CoerceTypeModifier;
import at.irian.ankor.serialization.modify.Modifier;
import at.irian.ankor.serialization.modify.PassThroughModifier;
import at.irian.ankor.session.*;
import at.irian.ankor.state.StateHolderViewModelPostProcessor;
import at.irian.ankor.switching.AkkaConsistentHashingSwitchboard;
import at.irian.ankor.switching.DefaultSwitchboard;
import at.irian.ankor.switching.Switchboard;
import at.irian.ankor.switching.SwitchboardImplementor;
import at.irian.ankor.switching.connector.ConnectorLoader;
import at.irian.ankor.switching.connector.local.SessionModelAddressBinding;
import at.irian.ankor.switching.routing.DefaultServerRoutingLogic;
import at.irian.ankor.switching.routing.RoutingLogic;
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

    private static final String MESSAGE_MAPPER_CONFIG_KEY = "at.irian.ankor.serialization.MessageMapper";

    private String systemName;
    private Map<String, Object> configValues;
    private List<ViewModelPostProcessor> viewModelPostProcessors;
    private EventDispatcherFactory eventDispatcherFactory;
    private Scheduler scheduler;
    private ModelSessionFactory modelSessionFactory;
    private Application application;
    private BeanResolver beanResolver;
    private SwitchboardImplementor switchboard;
    private RefContextFactoryProvider refContextFactoryProvider;
    private BeanMetadataProvider beanMetadataProvider;
    private BeanFactory beanFactory;
    private RoutingLogic routingLogic;
    private boolean actorSystemEnabled;
    private ActorSystem actorSystem;
    private AnkorSystemMonitor monitor;
    private AnkorSystemStats stats;
    private ConnectorLoader connectorLoader;

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
        this.routingLogic = null;
        this.actorSystemEnabled = false;
        this.actorSystem = null;
        this.monitor = null;
        this.stats = null;
        this.connectorLoader = null;
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
    public AnkorSystemBuilder withSwitchboard(SwitchboardImplementor switchboard) {
        this.switchboard = switchboard;
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

    @SuppressWarnings("UnusedDeclaration")
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

    public AnkorSystemBuilder withRoutingLogic(RoutingLogic routingLogic) {
        this.routingLogic = routingLogic;
        return this;
    }

    public AnkorSystemBuilder withActorSystemEnabled() {
        this.actorSystemEnabled = true;
        return this;
    }

    @SuppressWarnings("UnusedDeclaration")
    public AnkorSystemBuilder withMonitor(AnkorSystemMonitor monitor) {
        this.monitor = monitor;
        return this;
    }

    public AnkorSystemBuilder withStateless(boolean stateless) {
        return this;
    }

    public AnkorSystemBuilder withConnectorLoader(ConnectorLoader connectorLoader) {
        this.connectorLoader = connectorLoader;
        return this;
    }

    public AnkorSystem createServer() {

        SwitchboardImplementor switchboard = getServerSwitchboard();

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
                                                                         refContextFactory,
                                                                         application);

        ModelSessionManager modelSessionManager = getServerModelSessionManager(modelSessionFactory,
                                                                               application);

        if (!configValues.containsKey(MESSAGE_MAPPER_CONFIG_KEY)) {
            configValues.put(MESSAGE_MAPPER_CONFIG_KEY, ViewModelJsonMessageMapper.class.getName());
        }

        switchboard.setRoutingLogic(getServerRoutingLogic(modelSessionFactory,
                                                          modelSessionManager,
                                                          application));

        return new AnkorSystem(application,
                               getConfig(),
                               switchboard,
                               refContextFactory,
                               modelSessionManager,
                               modelSessionFactory,
                               modifier,
                               beanMetadataProvider,
                               scheduler,
                               getMonitor(),
                               getConnectorLoader());
    }

    public AnkorSystem createClient() {

        if (viewModelPostProcessors != null) {
            throw new IllegalStateException("viewModelPostProcessors not supported for client system");
        }

        SwitchboardImplementor switchboard = getClientSwitchboard();

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
                                                                         defaultEventListeners,
                                                                         refContextFactory,
                                                                         application);

        ModelSession modelSession = modelSessionFactory.createModelSession();

        ModelSessionManager modelSessionManager = new SingletonModelSessionManager() {
            @Override
            public void remove(ModelSession modelSession) {
                // ignore; // TODO WebSocket reconnect problem, we need a reconnect, instead of connect! (see WebSocketFxClientApplication.connectionManager)
            }
        };
        modelSessionManager.add(modelSession);

        if (!configValues.containsKey(MESSAGE_MAPPER_CONFIG_KEY)) {
            configValues.put(MESSAGE_MAPPER_CONFIG_KEY, SimpleTreeJsonMessageMapper.class.getName());
        }

        switchboard.setRoutingLogic(getClientRoutingLogic());

        return new AnkorSystem(application,
                               getConfig(),
                               switchboard,
                               refContextFactory,
                               modelSessionManager,
                               modelSessionFactory,
                               modifier,
                               beanMetadataProvider,
                               scheduler,
                               getMonitor(),
                               getConnectorLoader());
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

        SessionModelAddressBinding sessionModelAddressBinding = new SessionModelAddressBinding();

        // action event listener for sending action events to remote partner
        eventListeners.add(new RemoteNotifyActionEventListener(switchboard, modifier));

        // global change event listener for sending change events to remote partner
        eventListeners.add(new RemoteNotifyChangeEventListener(switchboard, modifier, sessionModelAddressBinding));

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
        list.add(new StateHolderViewModelPostProcessor());
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

    private SwitchboardImplementor getServerSwitchboard() {
        if (switchboard == null) {
            ActorSystem actorSystem = getActorSystem();
            if (actorSystem == null) {
                switchboard = DefaultSwitchboard.createForConcurrency();
            } else {
                switchboard = AkkaConsistentHashingSwitchboard.create(actorSystem, getMonitor().switchboard());
                //switchboard = AkkaAddressBoundSwitchboard.create(actorSystem, getMonitor().switchboard());
            }
        }
        return switchboard;
    }

    private SwitchboardImplementor getClientSwitchboard() {
        if (switchboard == null) {
            switchboard = DefaultSwitchboard.createForSingleThread();
        }
        return switchboard;
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
            ActorSystem actorSystem = getActorSystem();
            if (actorSystem == null) {
                scheduler = new SimpleScheduler();
            } else {
                scheduler = new AkkaScheduler(actorSystem);
            }
        }
        return scheduler;
    }

    private Config getConfig() {
        return ConfigFactory.parseMap(configValues, "AnkorSystemBuilder")
                .withFallback(ConfigFactory.load());
    }

    private EventDispatcherFactory getEventDispatcherFactory() {
        if (eventDispatcherFactory == null) {
            ActorSystem actorSystem = getActorSystem();
            if (actorSystem == null) {
                eventDispatcherFactory = new SynchronizedSimpleEventDispatcherFactory();
            } else {
                //eventDispatcherFactory = new AkkaConsistentHashingEventDispatcherFactory(actorSystem);
                eventDispatcherFactory = new AkkaSessionBoundEventDispatcherFactory(actorSystem);
            }
        }
        return eventDispatcherFactory;
    }

    private ModelSessionFactory getModelSessionFactory(EventDispatcherFactory eventDispatcherFactory,
                                                       EventListeners defaultEventListeners,
                                                       RefContextFactory refContextFactory,
                                                       Application application) {
        if (modelSessionFactory == null) {
            modelSessionFactory = new DefaultModelSessionFactory(eventDispatcherFactory,
                                                                 defaultEventListeners,
                                                                 refContextFactory,
                                                                 application);
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

    private RoutingLogic getServerRoutingLogic(ModelSessionFactory modelSessionFactory,
                                               ModelSessionManager modelSessionManager,
                                               Application userApplication) {
        if (routingLogic == null) {
            routingLogic = new DefaultServerRoutingLogic(modelSessionFactory, modelSessionManager)
                    .withApplication(new ConsoleApplication(getStats()))
                    .withApplication(userApplication);
        }
        return routingLogic;
    }

    private RoutingLogic getClientRoutingLogic() {
        if (routingLogic == null) {
            throw new IllegalStateException("No RoutingLogic declared - typical RoutingLogic for clients is called 'Fixed<connector-type>RoutingLogic'");
        }
        return routingLogic;
    }

    private ActorSystem getActorSystem() {
        if (actorSystemEnabled) {
            if (actorSystem == null) {
                LOG.info("Creating Akka Actor System...");
                actorSystem = ActorSystem.create("Ankor");
            }
            return actorSystem;
        } else {
            return null;
        }
    }

    private AnkorSystemMonitor getMonitor() {
        if (monitor == null) {
            ActorSystem actorSystem = getActorSystem();
            if (actorSystem == null) {
                monitor = new StatsAnkorSystemMonitor(getStats());
            } else {
                monitor = AkkaAnkorSystemMonitor.create(actorSystem, getStats());
            }
        }
        return monitor;
    }

    private AnkorSystemStats getStats() {
        if (stats == null) {
            stats = new AnkorSystemStats();
        }
        return stats;
    }

    private ModelSessionManager getServerModelSessionManager(ModelSessionFactory modelSessionFactory,
                                                             Application application) {
        return DefaultModelSessionManager.create();
    }

    private ConnectorLoader getConnectorLoader() {
        if (connectorLoader == null) {
            connectorLoader = new ConnectorLoader();
        }
        return connectorLoader;
    }

}
