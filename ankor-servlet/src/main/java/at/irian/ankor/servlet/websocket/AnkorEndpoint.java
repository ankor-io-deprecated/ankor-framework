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
import at.irian.ankor.session.RemoteSystem;
import at.irian.ankor.system.AnkorSystem;
import at.irian.ankor.system.AnkorSystemBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpointConfig;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This is the base class of a WebSocket endpoint that communicates with a {@link AnkorSystem}.
 * It is meant be deployed on a web server (e.g. GlassFish) that supports JSR 365 (javax.websocket).
 *
 * This class handles new WebSocket connections on a fixed url ("/websockets/ankor").
 * It passes incoming messages to the AnkorSystem and registers the connection with the AnkorSystem, so that
 * outgoing messages will be sent the the clients.
 *
 * As a user you need to subtype this class and override the {@link #getModelRoot(at.irian.ankor.ref.Ref)} method.
 *
 * @author Florian Klampfer
 */
public abstract class AnkorEndpoint extends Endpoint implements MessageHandler.Whole<String>, ServerApplicationConfig {
    private static Logger LOG = LoggerFactory.getLogger(AnkorEndpoint.class);

    /**
     * Must be greater than the clients heartbeat interval.
     */
    private static final int TIME_OUT = 60;

    private static AnkorSystem ankorSystem;
    private static WebSocketMessageBus webSocketMessageBus;

    private String clientId;
    private long lastSeen = System.currentTimeMillis();

    private final static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public AnkorEndpoint() {
        LOG.info("Creating new Endpoint");

        /*
         * XXX: This class gets created for every new WebSocket connection,
         * so using a cheap "singleton" for the ankorSystem to prevent reinitialization.
         *
         * This also means that the Ankor system will be created only when the first client connects to the server,
         * resulting in a short lag, so maybe have the AnkorSystem in a servlet and reference it from here?
         */
        if (ankorSystem == null) {
            startAnkorSystem();
        }
    }

    @Override
    public void onOpen(final Session session, EndpointConfig config) {
        clientId = session.getPathParameters().get("clientId");

        if (clientId == null) {
            LOG.error("Trying to connect without supplying an id");
            throw new IllegalArgumentException();
        }

        LOG.info("New client connected {}", clientId);

        session.addMessageHandler(this);
        webSocketMessageBus.addRemoteSystem(new WebSocketRemoteSystem(clientId, session));

        watchForTimeout(session);
    }

    /**
     * This checks if a (heartbeat) message has been received within the last {@code TIME_OUT} seconds,
     * otherwise the WebSocket connection is closed and the Ankor session is invalidated.
     *
     * @param session The WebSocket session.
     */
    private void watchForTimeout(final Session session) {
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - lastSeen > TIME_OUT * 1000) {
                    try {
                        // Invalidate the Ankor session.
                        invalidate(session);

                        // Close the web socket session if it isn't closed already.
                        if (session.isOpen()) {
                            session.close();
                        }
                    } catch (IOException ignored) {
                    }
                } else {
                    executor.schedule(this, TIME_OUT, TimeUnit.SECONDS);
                }
            }
        }, TIME_OUT, TimeUnit.SECONDS);
    }

    @Override
    public void onMessage(String message) {
        if (!isHeartbeat(message)) {
            LOG.info("Endpoint received {}, length = {}", message, message.length());

            // TODO: Validate message: A malformed message will crash this Ankor session (but new clients can connect)
            webSocketMessageBus.receiveSerializedMessage(message);
        }

        lastSeen = System.currentTimeMillis();
    }

    private boolean isHeartbeat(String message) {
        return message.trim().equals("");
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        invalidate(session);
    }

    @Override
    public void onError(Session session, Throwable thr) {
        invalidate(session);
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

    private void invalidate(Session session) {
        session.removeMessageHandler(this);
        RemoteSystem remoteSystem = webSocketMessageBus.removeRemoteSystem(this.clientId);
        invalidateAnkorSessionsFor(remoteSystem);
    }

    private void invalidateAnkorSessionsFor(RemoteSystem remoteSystem) {
        Collection<at.irian.ankor.session.Session> ankorSessions = ankorSystem.getSessionManager().getAllFor(remoteSystem);
        for(at.irian.ankor.session.Session ankorSession : ankorSessions) {
            ankorSystem.getSessionManager().invalidate(ankorSession);
        }
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
