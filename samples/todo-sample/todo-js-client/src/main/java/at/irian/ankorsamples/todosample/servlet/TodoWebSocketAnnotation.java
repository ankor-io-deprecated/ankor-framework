package at.irian.ankorsamples.todosample.servlet;

import at.irian.ankor.akka.AnkorActorSystem;
import at.irian.ankor.base.BeanResolver;
import at.irian.ankor.delay.AkkaScheduler;
import at.irian.ankor.event.dispatch.AkkaEventDispatcherFactory;
import at.irian.ankor.messaging.json.viewmodel.ViewModelJsonMessageMapper;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.session.ModelRootFactory;
import at.irian.ankor.system.AnkorSystem;
import at.irian.ankor.system.AnkorSystemBuilder;
import at.irian.ankorsamples.todosample.domain.task.TaskRepository;
import at.irian.ankorsamples.todosample.viewmodel.ModelRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

/*
 * TODO: To free the Ankor user from worrying about the transport a base class similar to the "AnkorServlet" should be provided.
 * However, the WebSocket annotations do not support inheritance, so switching to the EndPoint API is necessary.
 */
@ServerEndpoint(value = "/websocket/ankor")
public class TodoWebSocketAnnotation {
    private static Logger LOG = LoggerFactory.getLogger(TodoWebSocketAnnotation.class);

    // private final AtomicInteger connectionIds = new AtomicInteger(0);
    // private final Set<TodoWebSocketAnnotation> connections = new CopyOnWriteArraySet<TodoWebSocketAnnotation>();
    private static AnkorSystem ankorSystem;
    private static WebSocketMessageBus webSocketMessageBus;

    private String clientId;

    /*
     * XXX: This class gets created for every new websocket connection,
     * so using a cheap "singleton" for the ankorSystem to prevent reinitialization.
     */
    public TodoWebSocketAnnotation() {
        if (ankorSystem == null) {
            startAnkorSystem();
        }
    }

    @OnOpen
    public void start(Session session) throws IOException {
        clientId = UUID.randomUUID().toString();
        LOG.info("Client connected {}", clientId);

        webSocketMessageBus.addRemoteSystem(new WebSocketRemoteSystem(clientId, session));

        session.getBasicRemote().sendText(clientId);
    }

    // TODO: Check under which circumstances this function gets called
    @OnClose
    public void end() {
        LOG.info("Disconnecting {} because of close", clientId);
        webSocketMessageBus.removeRemoteSystem(clientId);
        LOG.info("Remote systems remaining: {}", webSocketMessageBus.getKnownRemoteSystems().size());
    }

    // TODO: Check under which circumstances this function gets called
    @OnError
    public void error(Throwable throwable) {
        LOG.info("Disconnecting something because of error");
    }

    @OnMessage
    public void incoming(String message) {
        webSocketMessageBus.receiveSerializedMessage(message);
    }

    private void startAnkorSystem() {
        AnkorActorSystem ankorActorSystem;
        ankorSystem = new AnkorSystemBuilder()
                .withName(getName())
                .withBeanResolver(getBeanResolver())
                .withModelRootFactory(getModelRootFactory())
                .withMessageBus((webSocketMessageBus = new WebSocketMessageBus(new ViewModelJsonMessageMapper())))
                .withDispatcherFactory(new AkkaEventDispatcherFactory((ankorActorSystem = AnkorActorSystem.create())))
                .withScheduler(new AkkaScheduler(ankorActorSystem))
                .createServer();
        ankorSystem.start();
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
