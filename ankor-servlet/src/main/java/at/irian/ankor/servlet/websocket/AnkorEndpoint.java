package at.irian.ankor.servlet.websocket;

import at.irian.ankor.akka.AnkorActorSystem;
import at.irian.ankor.base.BeanResolver;
import at.irian.ankor.delay.AkkaScheduler;
import at.irian.ankor.event.dispatch.AkkaEventDispatcherFactory;
import at.irian.ankor.messaging.json.viewmodel.ViewModelJsonMessageMapper;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.servlet.websocket.messaging.WebSocketMessageBus;
import at.irian.ankor.servlet.websocket.session.WebSocketRemoteSystem;
import at.irian.ankor.session.ModelRootFactory;
import at.irian.ankor.system.AnkorSystem;
import at.irian.ankor.system.AnkorSystemBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpointConfig;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * This is the base class of a WebSocket endpoint that communicates with a {@link AnkorSystem}.
 * It is meant reside on a web server (e.g. GlassFish) that supports JSR 365 (javax.websocket).
 *
 * This class handles new WebSocket connections on a fixed url ("/websockets/ankor"), which is also used by the JS
 * clients WebSocketTransport default implementation. It passes incoming messages to the AnkorSystem and registers
 * the connection with the AnkorSystem, so that  outgoing messages will be sent the the clients.
 *
 * As a user you need to subtype this class and override the {@link #getModelRoot(at.irian.ankor.ref.Ref)} method.
 *
 * @author Florian Klampfer
 */
// TODO: Session Management: ping-pongs, timeouts, heartbeat, etc...
public abstract class AnkorEndpoint extends Endpoint implements  MessageHandler.Whole<String>, ServerApplicationConfig {

    private static Logger LOG = LoggerFactory.getLogger(AnkorEndpoint.class);

    private static AnkorSystem ankorSystem;
    private static WebSocketMessageBus webSocketMessageBus;

    private String clientId;

    public AnkorEndpoint() {
        LOG.info("Creating new Endpoint");

        /*
         * XXX: This class gets created for every new websocket connection,
         * so using a cheap "singleton" for the ankorSystem to prevent reinitialization.
         *
         * This also means that the AnkorSystem will be created only when the first client connects to the server,
         * resulting in a short lag, so maybe have the AnkorSystem in a servlet and reference it from here?
         */
        if (ankorSystem == null) {
            startAnkorSystem();
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        clientId = session.getPathParameters().get("clientId");

        if (clientId == null) {
            LOG.error("Trying to connect without supplying an id");
            throw new IllegalArgumentException();
        }

        LOG.info(" New client connected {}", clientId);

        session.addMessageHandler(this);
        webSocketMessageBus.addRemoteSystem(new WebSocketRemoteSystem(clientId, session));
    }

    @Override
    public void onMessage(String message) {
        LOG.info("Endpoint received {}", message);
        webSocketMessageBus.receiveSerializedMessage(message);
    }

    @Override
    // TODO: Session Management: Check under which circumstances this function gets called
    public void onClose(Session session, CloseReason closeReason) {
        super.onClose(session, closeReason);
        webSocketMessageBus.removeRemoteSystem(clientId);

        // TODO: Session Management: Invalidate Ankor session
    }

    @Override
    // TODO: Session Management: Check under which circumstances this function gets called
    public void onError(Session session, Throwable thr) {
        super.onError(session, thr);
        webSocketMessageBus.removeRemoteSystem(clientId);
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

    /**
     * You must override this method in your implementation.
     *
     * @param rootRef The Ankor {@link Ref} to your root model.
     * @return The root model of your application.
     */
    protected abstract Object getModelRoot(Ref rootRef);

    /**
     * You can override this method in your implementation.
     * @return A name for the AnkorSystem on your server.
     */
    protected String getName() {
        return "ankor-servlet-server";
    }

    /**
     * You can override this method in your implementation.
     * @return A {@link ModelRootFactory} the provides the root model of your application.
     */
    protected ModelRootFactory getModelRootFactory() {
        return new ModelRootFactory() {
            @Override
            public Set<String> getKnownRootNames() {
                return Collections.singleton("root");
            }

            @Override
            public Object createModelRoot(Ref rootRef) {
                return getModelRoot(rootRef);
            }
        };
    }

    /**
     * You can override this method in your implementation.
     * @return A bean resolver for the Ankor system.
     */
    // XXX: Do we need this at all?
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

    /**
     * This method can be overridden to provide a custom url to the WebSocket.
     * Note that you need to specify this string on the client as well.
     *
     * @return a path string that starts with '/' but must not end with '/'.
     * Defaults to "/websocket/ankor".
     */
    protected String getWebSocketUrl() {
        return "/websocket/ankor";
    }

    @Override
    public final Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> endpointClasses) {
        // XXX: Only one WebSocket endpoint url (specified by getPath()) allowed in this servlet.
        return Collections.singleton(ServerEndpointConfig.Builder.create(this.getClass(), this.getWebSocketUrl() + "/{clientId}").build());
    }

    @Override
    public final Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned) {
        return Collections.emptySet();
    }
}
