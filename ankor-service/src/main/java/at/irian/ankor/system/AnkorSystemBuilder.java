package at.irian.ankor.system;

import at.irian.ankor.annotation.ViewModelAnnotationScanner;
import at.irian.ankor.context.DefaultModelContext;
import at.irian.ankor.context.ModelContext;
import at.irian.ankor.event.EventDelaySupport;
import at.irian.ankor.messaging.MessageBus;
import at.irian.ankor.messaging.MessageFactory;
import at.irian.ankor.model.ViewModelPostProcessor;
import at.irian.ankor.model.ViewModelPropertyFieldsInitializer;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefContextFactory;
import at.irian.ankor.ref.el.ELRefContextFactory;
import at.irian.ankor.session.*;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Manfred Geiler
 */
public class AnkorSystemBuilder {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorSystemBuilder.class);

    private String systemName;
    private Config config;
    private MessageFactory messageFactory;
    private List<ViewModelPostProcessor> viewModelPostProcessors;

    private ModelRootFactory modelRootFactory;
    private BeanResolver beanResolver;
    private MessageBus messageBus;

    public AnkorSystemBuilder() {
        this.systemName = null;
        this.config = ConfigFactory.load();
        this.messageFactory = new MessageFactory();
        this.viewModelPostProcessors = null;
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

    public AnkorSystem createServer() {

        if (systemName == null) {
            systemName = "Unnamed Server";
            LOG.warn("No system name specified, using default name {}", systemName);
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

        EventDelaySupport eventDelaySupport = new EventDelaySupport(systemName);

        RefContextFactory refContextFactory = new ELRefContextFactory(config,
                                                                      beanResolver,
                                                                      eventDelaySupport,
                                                                      viewModelPostProcessors);

        SessionFactory sessionFactory = new DefaultServerSessionFactory(modelRootFactory,
                                                                       refContextFactory,
                                                                       messageFactory);
        SessionManager sessionManager = new DefaultSessionManager(sessionFactory);

        return new AnkorSystem(systemName, messageFactory, messageBus, refContextFactory, sessionManager);
    }


    public AnkorSystem createClient() {

        if (systemName == null) {
            systemName = "Unnamed Client";
            LOG.warn("No system name specified, using default name {}", systemName);
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

        EventDelaySupport eventDelaySupport = new EventDelaySupport(systemName);

        RefContextFactory refContextFactory = new ELRefContextFactory(config,
                                                                      beanResolver,
                                                                      eventDelaySupport,
                                                                      viewModelPostProcessors);

        ModelContext modelContext = new DefaultModelContext(new HashMap());
        RefContext refContext = refContextFactory.createRefContextFor(modelContext);
        SessionManager sessionManager = new SingletonSessionManager(modelContext, refContext);
        Session session = sessionManager.getOrCreateSession(null);

        // action event listener for sending action events to remote partner
        modelContext.getModelEventListeners().add(new DefaultSyncActionEventListener(messageFactory, session));

        // global change event listener for sending change events to remote partner
        modelContext.getModelEventListeners().add(new DefaultSyncChangeEventListener(messageFactory, session));

        session.setMessageSender(messageBus);

        return new AnkorSystem(systemName, messageFactory, messageBus, refContextFactory, sessionManager);
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

        private static final String[] EMPTY_STRING_ARRAY = new String[0];

        @Override
        public Object resolveByName(String beanName) {
            return null;
        }

        @Override
        public String[] getBeanDefinitionNames() {
            return EMPTY_STRING_ARRAY;
        }
    }
}
