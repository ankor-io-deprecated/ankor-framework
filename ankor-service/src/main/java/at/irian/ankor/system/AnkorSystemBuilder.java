package at.irian.ankor.system;

import at.irian.ankor.annotation.ViewModelAnnotationScanner;
import at.irian.ankor.base.BeanResolver;
import at.irian.ankor.context.DefaultModelContextManager;
import at.irian.ankor.context.ModelContext;
import at.irian.ankor.context.ModelContextFactory;
import at.irian.ankor.context.SingletonModelContextManager;
import at.irian.ankor.event.EventListeners;
import at.irian.ankor.event.dispatch.EventDispatcherFactory;
import at.irian.ankor.event.dispatch.SynchronisedEventDispatcherFactory;
import at.irian.ankor.messaging.*;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefContextFactory;
import at.irian.ankor.ref.el.ELRefContextFactory;
import at.irian.ankor.session.*;
import at.irian.ankor.viewmodel.ViewModelPostProcessor;
import at.irian.ankor.viewmodel.ViewModelPropertyFieldsInitializer;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.*;

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

    private ModelRootFactory modelRootFactory;
    private BeanResolver beanResolver;
    private MessageBus messageBus;
    private ModelContextFactory modelContextFactory;

    public AnkorSystemBuilder() {
        this.systemName = null;
        this.config = ConfigFactory.load();
        this.viewModelPostProcessors = null;
        this.messageIdGenerator = null;
        this.eventDispatcherFactory = new SynchronisedEventDispatcherFactory();
        this.modelContextId = null;
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

    public AnkorSystem createServer() {

        if (systemName == null) {
            systemName = "Unnamed Server";
            LOG.warn("No system name specified, using default name {}", systemName);
        }

        if (messageIdGenerator == null) {
            messageIdGenerator = createDefaultMessageIdGenerator();
        }

        if (modelRootFactory == null) {
            throw new IllegalStateException("modelRootFactory not set");
        }

        if (messageBus == null) {
            throw new IllegalStateException("messageBus not set");
        }

        if (viewModelPostProcessors == null) {
            viewModelPostProcessors = createDefaultServerViewModelPostProcessors();
        }

        if (beanResolver == null) {
            beanResolver = new EmptyBeanResolver();
        }

        if (modelContextFactory == null) {
            modelContextFactory = new ModelContextFactory(eventDispatcherFactory);
        }

        DefaultModelContextManager modelContextManager = new DefaultModelContextManager(modelContextFactory);

        RefContextFactory refContextFactory = new ELRefContextFactory(config,
                                                                      beanResolver,
                                                                      viewModelPostProcessors);

        final MessageFactory messageFactory = new MessageFactory(systemName, messageIdGenerator);

        SessionFactory sessionFactory = new ServerSessionFactory(modelRootFactory,
                                                                 refContextFactory,
                                                                 eventDispatcherFactory) {
            @Override
            public ServerSession create(ModelContext modelContext, String sessionId) {
                ServerSession session = super.create(modelContext, sessionId);
                addDefaultEventListeners(session, messageFactory, messageBus);
                return session;
            }
        };

        SessionManager sessionManager = new DefaultSessionManager(sessionFactory);

        return new AnkorSystem(systemName, messageFactory, messageBus, refContextFactory,
                               modelContextManager,
                               sessionManager);
    }


    public AnkorSystem createClient() {

        if (systemName == null) {
            systemName = "Unnamed Client";
            LOG.warn("No system name specified, using default name {}", systemName);
        }

        if (messageIdGenerator == null) {
            messageIdGenerator = createDefaultMessageIdGenerator();
        }

        if (modelRootFactory != null) {
            throw new IllegalStateException("custom modelRootFactory not supported for client system");
        }
        modelRootFactory = new DefaultClientModelRootFactory();

        if (messageBus == null) {
            throw new IllegalStateException("messageBus not set");
        }

        if (viewModelPostProcessors != null) {
            throw new IllegalStateException("viewModelPostProcessors not supported for client system");
        }

        if (beanResolver != null) {
            throw new IllegalStateException("beanResolver not supported for client system");
        }
        beanResolver = new EmptyBeanResolver();

        if (modelContextFactory == null) {
            modelContextFactory = new ModelContextFactory(eventDispatcherFactory);
        }

        if (modelContextId == null) {
            modelContextId = "" + (++modelContextIdCnt);
        }

        RefContextFactory refContextFactory = new ELRefContextFactory(config,
                                                                      beanResolver,
                                                                      viewModelPostProcessors);

        SingletonModelContextManager modelContextManager
                = new SingletonModelContextManager(modelContextFactory, modelContextId);

        ModelContext modelContext = modelContextManager.getOrCreate(modelContextId);

        RefContext refContext = refContextFactory.createRefContextFor(modelContext);
        SessionManager sessionManager = new SingletonSessionManager(modelContext, refContext);
        Session session = sessionManager.getOrCreate(modelContext, systemName);

        MessageFactory messageFactory = new MessageFactory(systemName, messageIdGenerator);

        addDefaultEventListeners(session, messageFactory, messageBus);

        return new AnkorSystem(systemName, messageFactory, messageBus, refContextFactory,
                               modelContextManager,
                               sessionManager);
    }

    private CounterMessageIdGenerator createDefaultMessageIdGenerator() {
        return new CounterMessageIdGenerator(systemName.substring(0, 1) + "#");
    }

    private void addDefaultEventListeners(Session session, MessageFactory messageFactory, MessageSender messageSender) {

        EventListeners eventListeners = session.getModelContext().getEventListeners();

        // remote actions and changes listener
        eventListeners.add(new RemoteEventListener());

        // action event listener for sending action events to remote partner
        eventListeners.add(new DefaultSyncActionEventListener(messageFactory, messageSender));

        // global change event listener for sending change events to remote partner
        eventListeners.add(new DefaultSyncChangeEventListener(messageFactory, messageSender));

        // global change event listener for cleaning up obsolete listeners
        eventListeners.add(new ListenerCleanupChangeEventListener(eventListeners));
    }


    private List<ViewModelPostProcessor> createDefaultServerViewModelPostProcessors() {
        List<ViewModelPostProcessor> list = new ArrayList<ViewModelPostProcessor>();
        list.add(new ViewModelPropertyFieldsInitializer());
        list.add(new ViewModelAnnotationScanner());
        return list;
    }

    private static class DefaultClientModelRootFactory implements ModelRootFactory {
        @Override
        public Object createModelRoot(Ref rootRef) {
            return new HashMap();
        }
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
