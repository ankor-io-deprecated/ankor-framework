package at.irian.ankor.system;

import at.irian.ankor.annotation.ViewModelAnnotationScanner;
import at.irian.ankor.base.BeanResolver;
import at.irian.ankor.change.ChangeRequestEventListener;
import at.irian.ankor.context.*;
import at.irian.ankor.delay.Scheduler;
import at.irian.ankor.delay.SimpleScheduler;
import at.irian.ankor.delay.TaskRequestEventListener;
import at.irian.ankor.event.ArrayListEventListeners;
import at.irian.ankor.event.EventListeners;
import at.irian.ankor.event.dispatch.EventDispatcherFactory;
import at.irian.ankor.event.dispatch.SynchronisedEventDispatcherFactory;
import at.irian.ankor.messaging.*;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefContextFactory;
import at.irian.ankor.ref.el.ELRefContextFactory;
import at.irian.ankor.session.*;
import at.irian.ankor.viewmodel.ViewModelPostProcessor;
import at.irian.ankor.viewmodel.ViewModelPropertyFieldsInitializer;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Manfred Geiler
 */
public class AnkorSystemBuilder {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorSystemBuilder.class);

    private static int modelContextIdCnt = 0;

    private String systemName;
    private Config config;
    private List<ViewModelPostProcessor> viewModelPostProcessors;
    private MessageIdGenerator messageIdGenerator;
    private EventDispatcherFactory eventDispatcherFactory;
    private String modelContextId;
    private Scheduler scheduler;
    private EventListeners globalEventListeners;
    private ModelContextFactory modelContextFactory;

    private ModelRootFactory modelRootFactory;
    private BeanResolver beanResolver;
    private MessageBus messageBus;

    public AnkorSystemBuilder() {
        this.systemName = null;
        this.config = ConfigFactory.load();
        this.viewModelPostProcessors = null;
        this.messageIdGenerator = null;
        this.eventDispatcherFactory = null;
        this.modelContextId = null;
        this.scheduler = null;
        this.globalEventListeners = null;
        this.modelContextFactory = null;
    }

    public AnkorSystemBuilder withName(String name) {
        this.systemName = name;
        return this;
    }

    public AnkorSystemBuilder withModelRootFactory(ModelRootFactory modelRootFactory) {
        this.modelRootFactory = modelRootFactory;
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


    public AnkorSystem createServer() {

        String systemName = getServerSystemName();
        MessageBus messageBus = getMessageBus();
        EventDispatcherFactory eventDispatcherFactory = getEventDispatcherFactory();

        RefContextFactory refContextFactory = new ELRefContextFactory(config,
                                                                      getServerBeanResolver(),
                                                                      getServerViewModelPostProcessors(),
                                                                      getScheduler());

        MessageFactory messageFactory = new MessageFactory(systemName, getMessageIdGenerator());

        SessionFactory sessionFactory = new ServerSessionFactory(getServerModelRootFactory(),
                                                                 refContextFactory,
                                                                 eventDispatcherFactory,
                                                                 messageBus);
        SessionManager sessionManager = new DefaultSessionManager(sessionFactory);

        EventListeners globalEventListeners = getGlobalEventListeners(messageFactory, sessionManager);

        ModelContextFactory modelContextFactory = getModelContextFactory(eventDispatcherFactory, globalEventListeners);

        ModelContextManager modelContextManager = new DefaultModelContextManager(modelContextFactory);

        return new AnkorSystem(systemName, messageFactory, messageBus, refContextFactory,
                               modelContextManager,
                               sessionManager);
    }



    public AnkorSystem createClient() {

        if (modelRootFactory != null) {
            throw new IllegalStateException("custom modelRootFactory not supported for client system");
        }

        if (viewModelPostProcessors != null) {
            throw new IllegalStateException("viewModelPostProcessors not supported for client system");
        }

        String systemName = getClientSystemName();
        MessageBus messageBus = getMessageBus();

        RefContextFactory refContextFactory = new ELRefContextFactory(config,
                                                                      getClientBeanResolver(),
                                                                      null,
                                                                      getScheduler());

        MessageFactory messageFactory = new MessageFactory(systemName, getMessageIdGenerator());

        String modelContextId = getModelContextId();

        SingletonSessionManager sessionManager = new SingletonSessionManager();
        EventListeners globalEventListeners = getGlobalEventListeners(messageFactory, sessionManager);

        ModelContextFactory modelContextFactory = getModelContextFactory(getEventDispatcherFactory(), globalEventListeners);

        ModelContext modelContext = modelContextFactory.createModelContext(modelContextId);

        RefContext refContext = refContextFactory.createRefContextFor(modelContext);

        RemoteSystem remoteSystem = getSingletonRemoteSystem(messageBus);

        MessageSender messageSender = messageBus.getMessageSenderFor(remoteSystem);

        sessionManager.setSession(new SingletonSession(modelContext, refContext, messageSender));

        ModelContextManager modelContextManager = new SingletonModelContextManager(modelContextId, modelContext);

        return new AnkorSystem(systemName, messageFactory, messageBus, refContextFactory,
                               modelContextManager,
                               sessionManager);
    }



    private EventListeners createDefaultGLobalEventListeners(MessageFactory messageFactory,
                                                             SessionManager sessionManager) {

        EventListeners eventListeners = new ArrayListEventListeners();

        // remote actions and changes listener
        eventListeners.add(new RemoteEventListener());

        // action event listener for sending action events to remote partner
        eventListeners.add(new RemoteNotifyActionEventListener(messageFactory, sessionManager));

        // global change event listener for sending change events to remote partner
        eventListeners.add(new RemoteNotifyChangeEventListener(messageFactory, sessionManager));

        // global change event listener for cleaning up obsolete listeners
        eventListeners.add(new ListenerCleanupChangeEventListener(eventListeners));

        // global change request event listener
        eventListeners.add(new ChangeRequestEventListener());

        // global task request event listener
        eventListeners.add(new TaskRequestEventListener());

        return eventListeners;
    }


    private List<ViewModelPostProcessor> createDefaultServerViewModelPostProcessors() {
        List<ViewModelPostProcessor> list = new ArrayList<ViewModelPostProcessor>();
        list.add(new ViewModelPropertyFieldsInitializer());
        list.add(new ViewModelAnnotationScanner());
        return list;
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

    private String getServerSystemName() {
        if (systemName == null) {
            LOG.warn("No system name specified, using default name {}", systemName);
            return "Unnamed Server";
        }
        return systemName;
    }

    private String getClientSystemName() {
        if (systemName == null) {
            LOG.warn("No system name specified, using default name {}", systemName);
            return "Unnamed Client";
        }
        return systemName;
    }

    private MessageIdGenerator getMessageIdGenerator() {
        if (messageIdGenerator == null) {
            return createDefaultMessageIdGenerator();
        }
        return this.messageIdGenerator;
    }

    private MessageIdGenerator createDefaultMessageIdGenerator() {
        return new CounterMessageIdGenerator(systemName + "#");
    }

    private ModelRootFactory getServerModelRootFactory() {
        if (modelRootFactory == null) {
            throw new IllegalStateException("modelRootFactory not set");
        }
        return this.modelRootFactory;
    }

    private MessageBus getMessageBus() {
        if (messageBus == null) {
            throw new IllegalStateException("messageBus not set");
        }
        return this.messageBus;
    }

    private List<ViewModelPostProcessor> getServerViewModelPostProcessors() {
        if (viewModelPostProcessors == null) {
            return createDefaultServerViewModelPostProcessors();
        }
        return this.viewModelPostProcessors;
    }

    private BeanResolver getClientBeanResolver() {
        if (beanResolver != null) {
            throw new IllegalStateException("beanResolver not supported for client system");
        }
        return new EmptyBeanResolver();
    }

    private BeanResolver getServerBeanResolver() {
        if (beanResolver == null) {
            return new EmptyBeanResolver();
        }
        return this.beanResolver;
    }

    private Scheduler getScheduler() {
        if (scheduler == null) {
            return new SimpleScheduler();
        }
        return this.scheduler;
    }

    private EventDispatcherFactory getEventDispatcherFactory() {
        if (eventDispatcherFactory == null) {
            return new SynchronisedEventDispatcherFactory();
        }
        return this.eventDispatcherFactory;
    }

    private EventListeners getGlobalEventListeners(MessageFactory messageFactory, SessionManager sessionManager) {
        if (globalEventListeners == null) {
            return createDefaultGLobalEventListeners(messageFactory, sessionManager);
        }
        return this.globalEventListeners;
    }

    private ModelContextFactory getModelContextFactory(EventDispatcherFactory eventDispatcherFactory,
                                                       EventListeners globalEventListeners) {
        if (modelContextFactory == null) {
            return new DefaultModelContextFactory(eventDispatcherFactory, globalEventListeners);
        }
        return this.modelContextFactory;
    }

    private String getModelContextId() {
        if (modelContextId == null) {
            return "" + (++modelContextIdCnt);
        }
        return this.modelContextId;
    }

    private RemoteSystem getSingletonRemoteSystem(MessageBus messageBus) {
        Collection<? extends RemoteSystem> remoteSystems = messageBus.getKnownRemoteSystems();
        if (remoteSystems.size() != 1) {
            throw new IllegalStateException("None or multiple remote systems?");
        }
        return remoteSystems.iterator().next();
    }

}
