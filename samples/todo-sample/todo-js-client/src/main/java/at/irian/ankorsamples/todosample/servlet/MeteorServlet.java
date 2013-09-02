package at.irian.ankorsamples.todosample.servlet;

import at.irian.ankor.akka.AnkorActorSystem;
import at.irian.ankor.base.BeanResolver;
import at.irian.ankor.context.ModelContext;
import at.irian.ankor.delay.AkkaScheduler;
import at.irian.ankor.event.EventListeners;
import at.irian.ankor.event.dispatch.AkkaEventDispatcherFactory;
import at.irian.ankor.messaging.Message;
import at.irian.ankor.messaging.json.viewmodel.ViewModelJsonMessageMapper;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.servlet.AnkorServlet;
import at.irian.ankor.servlet.RequestFinishedEvent;
import at.irian.ankor.servlet.ServletMessageBus;
import at.irian.ankor.session.ModelRootFactory;
import at.irian.ankor.system.AnkorSystem;
import at.irian.ankor.system.AnkorSystemBuilder;
import at.irian.ankorsamples.todosample.domain.task.TaskRepository;
import at.irian.ankorsamples.todosample.viewmodel.ModelRoot;
import org.atmosphere.config.service.MeteorService;
import org.atmosphere.cpr.*;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@MeteorService(path = "/*", interceptors = {AtmosphereResourceLifecycleInterceptor.class})
public class MeteorServlet extends HttpServlet {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorServlet.class);

    private AnkorSystem ankorSystem;
    private Meteor meteor;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        AnkorActorSystem ankorActorSystem = AnkorActorSystem.create();
        ankorSystem = new AnkorSystemBuilder()
                .withName(getName())
                .withBeanResolver(getBeanResolver())
                .withModelRootFactory(getModelRootFactory())
                .withMessageBus(new ServletMessageBus(new ViewModelJsonMessageMapper()))
                .withDispatcherFactory(new AkkaEventDispatcherFactory(ankorActorSystem))
                .withScheduler(new AkkaScheduler(ankorActorSystem))
                .createServer();

        ankorSystem.start();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        meteor = Meteor.build(req).addListener(new AtmosphereResourceEventListenerAdapter());
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String serializedMessages = req.getReader().readLine().trim();

        ServletMessageBus messageBus = (ServletMessageBus) ankorSystem.getMessageBus();
        ViewModelJsonMessageMapper messageMapper = messageBus.getMessageMapper();

        // receive messages
        Message[] messages = messageMapper.deserializeArray(serializedMessages);
        Set<String> requestModelIds = new HashSet<String>();

        String clientId = null;
        for (Message message : messages) {
            clientId = message.getSenderId();

            requestModelIds.add(message.getModelId());
            LOG.debug("received: {}", message);
            messageBus.receiveMessage(message);
        }

        // send (response) messages
        if (clientId != null) {

            // wait for incoming messages being processed
            waitForMessagesProcessed(requestModelIds);

            // send all response messages
            Collection<Message> pendingMessages = messageBus.getPendingMessagesFor(clientId);
            Message[] messagesToSend = pendingMessages.toArray(new Message[pendingMessages.size()]);
            String responseMessages = messageMapper.serializeArray(messagesToSend);

            meteor.broadcast(responseMessages);
        }
    }

    private void waitForMessagesProcessed(Set<String> requestedModelIds) {

        if (requestedModelIds.isEmpty()) {
            return;
        }

        // queue special event for signalling that server finished the event processing for each model
        final BlockingQueue<String> finishedModelRequests = new LinkedBlockingQueue<String>();
        for (final String modelId : requestedModelIds) {

            ModelContext modelContext = ankorSystem.getModelContextManager().getOrCreate(modelId);
            final EventListeners eventListeners = modelContext.getEventListeners();
            eventListeners.add(new RequestFinishedEvent.Listener() {
                @Override
                public boolean isDiscardable() {
                    return false;
                }

                @Override
                public void processRequestFinished(RequestFinishedEvent requestFinishedEvent) {
                    finishedModelRequests.add(modelId);
                    eventListeners.remove(this);
                }
            });

            modelContext.getEventDispatcher().dispatch(new RequestFinishedEvent(this));
        }

        boolean interrupted = false;

        Set<String> outstandingModelIds = new HashSet<String>(requestedModelIds);
        while (!outstandingModelIds.isEmpty()) {
            try {
                String finishedModelId = finishedModelRequests.take();
                if (!outstandingModelIds.remove(finishedModelId)) {
                    throw new IllegalStateException("not waiting for " + finishedModelId);
                }
            } catch (InterruptedException e) {
                interrupted = true;
            }
        }

        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    protected String getName() {
        return "sample-todo-servlet-server";
    }

    protected BeanResolver getBeanResolver() {
        return new BeanResolver() {
            @Override
            public Object resolveByName(String beanName) {
                return null;
            }

            @Override
            public Collection<String> getKnownBeanNames() {
                return Collections.emptyList();
            }
        };
    }

    protected ModelRootFactory getModelRootFactory() {
        return new ModelRootFactory() {

            @Override
            public Set<String> getKnownRootNames() {
                return Collections.singleton("root");
            }

            @Override
            public Object createModelRoot(Ref rootRef) {
                return new ModelRoot(rootRef, new TaskRepository());
            }
        };
    }
}
