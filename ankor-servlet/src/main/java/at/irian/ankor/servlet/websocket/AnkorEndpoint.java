package at.irian.ankor.servlet.websocket;

import at.irian.ankor.akka.AnkorActorSystem;
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
import java.util.UUID;
import java.util.concurrent.*;

/**
 * This is the base class of a WebSocket endpoint that communicates with a {@link AnkorSystem}.
 * It is meant be deployed on a web server (e.g. GlassFish) that supports JSR 365 (javax.websocket).
 * <p/>
 * This class handles new WebSocket connections on a fixed url ("/websockets/ankor").
 * It passes incoming messages to the AnkorSystem and registers the connection with the AnkorSystem, so that
 * outgoing messages will be sent the the clients.
 * <p/>
 * As a user you need to subtype this class and override the {@link #getModelRoot(at.irian.ankor.ref.Ref)} method.
 *
 * @author Florian Klampfer
 */
public abstract class AnkorEndpoint extends Endpoint implements MessageHandler.Whole<String>, ServerApplicationConfig {
    /**
     * Must be greater than the clients heartbeat interval.
     */
    private static final int TIMEOUT = 30000;
    /**
     * The frequency of timeout checks.
     */
    private static final int TIMEOUT_CHECK_INTERVAL = 15000;
    private static Logger LOG = LoggerFactory.getLogger(AnkorEndpoint.class);
    private static ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
    private static volatile boolean created = false;
    private static AnkorSystem ankorSystem;
    private static WebSocketMessageBus webSocketMessageBus;
    private static Set<String> uniqueIds = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    private String clientId;
    private WebSocketRemoteSystem remoteSystem;

    public AnkorEndpoint() {
        LOG.info("Creating new Endpoint");

        if (!created) {
            synchronized (AnkorEndpoint.class) {
                if (!created) {
                    startAnkorSystem();
                    watchForTimeout();
                    created = true;
                }
            }
        }
    }

    @Override
    public void onOpen(final Session session, EndpointConfig config) {

        try {
            clientId = UUID.randomUUID().toString();

            if (clientId == null) {
                LOG.error("Trying to connect without supplying an id");
                throw new IllegalArgumentException();
            } else if (uniqueIds.contains(clientId)) {
                LOG.error("Id is not unique");
                throw new IllegalArgumentException();
            }

            uniqueIds.add(clientId);
            session.getBasicRemote().sendText(clientId);
            LOG.info("New client connected {}", clientId);

            remoteSystem = new WebSocketRemoteSystem(clientId, session);
            webSocketMessageBus.addRemoteSystem(remoteSystem);
            LOG.info("Number of connected clients: {}", webSocketMessageBus.getKnownRemoteSystems().size());

            remoteSystem.setLastSeen(System.currentTimeMillis());
            // watchForTimeout(session);

            session.addMessageHandler(this);
        } catch (IOException e) {
            LOG.error("Error while sending id to newly connected client");
        }
    }

    /**
     * This checks if a (heartbeat) message has been received within the last {@code TIMEOUT} milliseconds,
     * otherwise the WebSocket connection is closed and the Ankor session is invalidated.
     */
    private void watchForTimeout() {

        timer.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                for (RemoteSystem rs : webSocketMessageBus.getKnownRemoteSystems()) {
                    WebSocketRemoteSystem remoteSystem = (WebSocketRemoteSystem) rs;
                    long now = System.currentTimeMillis();
                    long lastSeen = remoteSystem.getLastSeen();
                    Session session = remoteSystem.getClient();

                    if (now - lastSeen > TIMEOUT) {
                        if (session.isOpen()) {
                            try {
                                session.close(new CloseReason(CloseReason.CloseCodes.GOING_AWAY, "Timeout"));
                            } catch (IOException e) {
                                LOG.error("Error while closing the connection after timeout");
                            }
                        }
                    }
                }
            }
        }, TIMEOUT_CHECK_INTERVAL, TIMEOUT_CHECK_INTERVAL, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onMessage(String message) {
        if (!isHeartbeat(message)) {
            LOG.info("Endpoint received {}, length = {}", message, message.length());

            // XXX: A malformed message will crash this Ankor session (but new clients can connect)
            webSocketMessageBus.receiveSerializedMessage(message);
        }

        remoteSystem.setLastSeen(System.currentTimeMillis());
    }

    private boolean isHeartbeat(String message) {
        return message.trim().equals("");
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        LOG.info("Invalidating session {} because of {}: {}", clientId, closeReason.getCloseCode(),
                closeReason.getReasonPhrase());
        this.invalidate();
        LOG.info("Number of connected clients: {}", webSocketMessageBus.getKnownRemoteSystems().size());
    }

    @Override
    public void onError(Session session, Throwable thr) {
        LOG.error("Invalidating session {} because of error {}", clientId, thr.getMessage());
        this.invalidate();
        LOG.info("Number of connected clients: {}", webSocketMessageBus.getKnownRemoteSystems().size());
    }

    private void startAnkorSystem() {
        AnkorActorSystem ankorActorSystem;
        ankorSystem = new AnkorSystemBuilder()
                .withName(getName())
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
     *
     * @return A name for the AnkorSystem on your server.
     */
    protected String getName() {
        return "ankor-servlet-server";
    }

    /**
     * You can override this method in your implementation.
     *
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
     * This method can be overridden to provide a custom url to the WebSocket.
     * Note that you need to specify this string on the client as well.
     *
     * @return a path string that starts with '/' but must not end with '/'.
     *         Defaults to "/websocket/ankor".
     */
    protected String getWebSocketUrl() {
        return "/websocket/ankor";
    }

    @Override
    public final Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> endpointClasses) {
        // XXX: Only one WebSocket endpoint url (specified by #getWebSocketUrl()) allowed by this servlet.
        return Collections.singleton(ServerEndpointConfig.Builder.create(this.getClass(), this.getWebSocketUrl()).build());
    }

    @Override
    public final Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned) {
        return Collections.emptySet();
    }

    private void invalidate() {
        RemoteSystem remoteSystem = webSocketMessageBus.removeRemoteSystem(clientId);
        Collection<at.irian.ankor.session.Session> ankorSessions = ankorSystem.getSessionManager().getAllFor(remoteSystem);
        for (at.irian.ankor.session.Session ankorSession : ankorSessions) {
            ankorSystem.getSessionManager().invalidate(ankorSession);
        }

    }
}

