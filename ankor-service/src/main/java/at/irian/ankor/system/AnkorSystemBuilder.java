package at.irian.ankor.system;

import at.irian.ankor.annotation.AnnotationViewModelPostProcessor;
import at.irian.ankor.base.BeanResolver;
import at.irian.ankor.change.ChangeRequestEventListener;
import at.irian.ankor.context.*;
import at.irian.ankor.delay.Scheduler;
import at.irian.ankor.delay.SimpleScheduler;
import at.irian.ankor.delay.TaskRequestEventListener;
import at.irian.ankor.event.ArrayListEventListeners;
import at.irian.ankor.event.EventListeners;
import at.irian.ankor.event.ModelEventListener;
import at.irian.ankor.event.dispatch.EventDispatcherFactory;
import at.irian.ankor.event.dispatch.SynchronisedEventDispatcherFactory;
import at.irian.ankor.messaging.*;
import at.irian.ankor.messaging.json.viewmodel.JacksonAnnotationAwareChangeModifier;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefContextFactory;
import at.irian.ankor.ref.el.ELRefContextFactory;
import at.irian.ankor.session.*;
import at.irian.ankor.viewmodel.ViewModelPostProcessor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.*;

/**
 * @author Manfred Geiler
 */
@SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
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
    private EventListeners defaultGlobalEventListeners;
    private ModelContextFactory modelContextFactory;

    private ModelRootFactory modelRootFactory;
    private BeanResolver beanResolver;
    private MessageBus messageBus;

    private List<ModelEventListener> customGlobalEventListeners;

    public AnkorSystemBuilder() {
        this.systemName = null;
        this.config = ConfigFactory.load();
        this.viewModelPostProcessors = null;
        this.messageIdGenerator = null;
        this.eventDispatcherFactory = null;
        this.modelContextId = null;
        this.scheduler = null;
        this.customGlobalEventListeners = new ArrayList<ModelEventListener>();
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

    public AnkorSystemBuilder withModelContextId(String modelContextId) {
        this.modelContextId = modelContextId;
        return this;
    }

    public AnkorSystem createServer() {

        String systemName = getServerSystemName();
        MessageBus messageBus = getMessageBus();
        EventDispatcherFactory eventDispatcherFactory = getEventDispatcherFactory();

        ModelRootFactory modelRootFactory = getServerModelRootFactory();

        RefContextFactory refContextFactory = new ELRefContextFactory(getServerBeanResolver(),
                                                                      getServerViewModelPostProcessors(),
                                                                      getScheduler(),
                                                                      modelRootFactory);

        MessageFactory messageFactory = getServerMessageFactory();

        SessionFactory sessionFactory = new ServerSessionFactory(
                refContextFactory,
                                                                 eventDispatcherFactory,
                                                                 messageBus);
        SessionManager sessionManager = new DefaultSessionManager(sessionFactory);

        ChangeModifier changeModifier = new JacksonAnnotationAwareChangeModifier();

        EventListeners globalEventListeners = getGlobalEventListeners(messageFactory, sessionManager, modelRootFactory,
                                                                      changeModifier);

        ModelContextFactory modelContextFactory = getModelContextFactory(eventDispatcherFactory, globalEventListeners);

        ModelContextManager modelContextManager = new DefaultModelContextManager(modelContextFactory);

        return new AnkorSystem(systemName, messageFactory, messageBus, refContextFactory,
                               modelContextManager,
                               sessionManager,
                               modelRootFactory);
    }


    public AnkorSystem createClient() {

        if (viewModelPostProcessors != null) {
            throw new IllegalStateException("viewModelPostProcessors not supported for client system");
        }

        String systemName = getClientSystemName();
        MessageBus messageBus = getMessageBus();

        ModelRootFactory modelRootFactory = getClientModelRootFactory();

        RefContextFactory refContextFactory = new ELRefContextFactory(getClientBeanResolver(),
                                                                      null,
                                                                      getScheduler(),
                                                                      modelRootFactory);

        MessageFactory messageFactory = getClientMessageFactory();

        String modelContextId = getModelContextId();

        SingletonSessionManager sessionManager = new SingletonSessionManager();
        ChangeModifier changeModifier = new ChangeModifier.PassThrough();

        EventListeners globalEventListeners = getGlobalEventListeners(messageFactory, sessionManager, modelRootFactory,
                                                                      changeModifier);

        ModelContextFactory modelContextFactory = getModelContextFactory(getEventDispatcherFactory(), globalEventListeners);

        ModelContext modelContext = modelContextFactory.createModelContext(modelContextId);

        RefContext refContext = refContextFactory.createRefContextFor(modelContext);

        RemoteSystem remoteSystem = getSingletonRemoteSystem(messageBus);

        MessageSender messageSender = messageBus.getMessageSenderFor(remoteSystem);

        sessionManager.setSession(new SingletonSession(modelContext, refContext, messageSender));

        ModelContextManager modelContextManager = new SingletonModelContextManager(modelContextId, modelContext);

        return new AnkorSystem(systemName, messageFactory, messageBus, refContextFactory,
                               modelContextManager,
                               sessionManager,
                               modelRootFactory);
    }



    private EventListeners createDefaultGlobalEventListeners(MessageFactory messageFactory,
                                                             SessionManager sessionManager,
                                                             ModelRootFactory modelRootFactory,
                                                             ChangeModifier changeModifier) {

        EventListeners eventListeners = new ArrayListEventListeners();

        // remote actions and changes listener
        eventListeners.add(new RemoteEventListener());

        // action event listener for sending action events to remote partner
        eventListeners.add(new RemoteNotifyActionEventListener(messageFactory, sessionManager));

        // global change event listener for sending change events to remote partner
        eventListeners.add(new RemoteNotifyChangeEventListener(messageFactory, sessionManager, modelRootFactory,
                                                               changeModifier));

        // global change event listener for cleaning up obsolete model context listeners
        eventListeners.add(new ListenerCleanupChangeEventListener());

        // global change request event listener
        eventListeners.add(new ChangeRequestEventListener());

        // global task request event listener
        eventListeners.add(new TaskRequestEventListener());

        return eventListeners;
    }



    private List<ViewModelPostProcessor> createDefaultServerViewModelPostProcessors() {
        List<ViewModelPostProcessor> list = new ArrayList<ViewModelPostProcessor>();
        list.add(new AnnotationViewModelPostProcessor());
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
            systemName = "Unnamed Server";
        }
        return systemName;
    }

    private String getClientSystemName() {
        if (systemName == null) {
            LOG.warn("No system name specified, using default name {}", systemName);
            systemName = "Unnamed Client";
        }
        return systemName;
    }

    public MessageIdGenerator getMessageIdGenerator() {
        if (messageIdGenerator == null) {
            messageIdGenerator = createDefaultMessageIdGenerator();
        }
        return messageIdGenerator;
    }

    private MessageIdGenerator createDefaultMessageIdGenerator() {
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
                                                  SessionManager sessionManager,
                                                  ModelRootFactory modelRootFactory,
                                                  ChangeModifier changeModifier) {
        EventListeners globalEventListeners;
        if (defaultGlobalEventListeners == null) {
            globalEventListeners = createDefaultGlobalEventListeners(messageFactory, sessionManager, modelRootFactory,
                                                                     changeModifier);
        } else {
            globalEventListeners = defaultGlobalEventListeners;
        }

        for (ModelEventListener customGlobalEventListener : customGlobalEventListeners) {
            globalEventListeners.add(customGlobalEventListener);
        }

        return globalEventListeners;
    }

    private ModelContextFactory getModelContextFactory(EventDispatcherFactory eventDispatcherFactory,
                                                       EventListeners globalEventListeners) {
        if (modelContextFactory == null) {
            modelContextFactory = new DefaultModelContextFactory(eventDispatcherFactory,
                                                                 globalEventListeners);
        }
        return modelContextFactory;
    }

    private String getModelContextId() {
        if (modelContextId == null) {
            modelContextId = "" + (++modelContextIdCnt);
        }
        return modelContextId;
    }

    private RemoteSystem getSingletonRemoteSystem(MessageBus messageBus) {
        Collection<? extends RemoteSystem> remoteSystems = messageBus.getKnownRemoteSystems();
        if (remoteSystems.size() != 1) {
            throw new IllegalStateException("None or multiple remote systems?");
        }
        return remoteSystems.iterator().next();
    }

    public MessageFactory getServerMessageFactory() {
        return new MessageFactory(getServerSystemName(), getMessageIdGenerator());
    }

    public MessageFactory getClientMessageFactory() {
        return new MessageFactory(getClientSystemName(), getMessageIdGenerator());
    }
}
