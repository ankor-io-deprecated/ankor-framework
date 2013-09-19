package at.irian.ankor.servlet.websocket;

import at.irian.ankor.akka.AnkorActorSystem;
import at.irian.ankor.base.BeanResolver;
import at.irian.ankor.delay.AkkaScheduler;
import at.irian.ankor.event.dispatch.AkkaEventDispatcherFactory;
import at.irian.ankor.messaging.json.viewmodel.ViewModelJsonMessageMapper;
import at.irian.ankor.servlet.websocket.messaging.WebSocketMessageBus;
import at.irian.ankor.servlet.websocket.session.WebSocketRemoteSystem;
import at.irian.ankor.session.ModelRootFactory;
import at.irian.ankor.system.AnkorSystem;
import at.irian.ankor.system.AnkorSystemBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.websocket.*;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;

/**
 * This is the base class of a WebSocket endpoint that communicates with a {@link AnkorSystem}.
 * It is meant reside on a web server (e.g. GlassFish) that supports JSR 365 (javax.websocket).
 *
 * This class handles new WebSocket connections on a fixed url ("/websockets/ankor"), which is also used by the JS
 * clients WebSocketTransport default implementation. It passes incoming messages to the AnkorSystem and registers
 * the connection with the AnkorSystem, so that  outgoing messages will be sent the the clients.
 *
 * As a user you need to subtype this class and override a couple of methods, most notably
 * {@link #getModelRootFactory()}. This factory provides the base level model, which is unique to your application.
 *
 * Note that you must annotate your implementation with the {@link javax.servlet.annotation.WebListener} annotation.
 *
 * @author Florian Klampfer
 */
// TODO: Actual session management via ping-pongs or timeouts, etc...
public abstract class AnkorEndpoint extends Endpoint implements MessageHandler.Whole<String>, ServletContextListener {

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
         * resulting in a short lag, so maybe have the AnkorSystem in a servlet and reference from here?
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
    // TODO: Check under which circumstances this function gets called
    public void onClose(Session session, CloseReason closeReason) {
        super.onClose(session, closeReason);
        webSocketMessageBus.removeRemoteSystem(clientId);
    }

    @Override
    // TODO: Check under which circumstances this function gets called
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
     * You can override this method in your implementation.
     * @return A name for the AnkorSystem on your server.
     */
    protected String getName() {
        return "ankor-servlet-server";
    }

    /**
     * You must override this method in your implementation.
     * XXX: I don't know what this is.
     * @return
     */
    protected abstract BeanResolver getBeanResolver();

    /**
     * You must override this method in your implementation.
     * @return A {@link ModelRootFactory} the provides the root model of your application.
     */
    // TODO: This can probably be made even easier by just exposing createModelRoot method to the subtype.
    protected abstract ModelRootFactory getModelRootFactory();

    /**
     * This method can be overridden to provide a custom url to the WebSocket.
     * Note that you need to specify this string on the client as well.
     *
     * @return a string that starts with '/'
     */
    protected String getWebSocketUrl() {
        return "/websocket/ankor";
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServerContainer sc = (ServerContainer) sce.getServletContext().getAttribute("javax.websocket.server.ServerContainer");
        addWebSocketEndpoint(sc);
    }

    /**
     * Register this class (meaning a subtype) as the web socket {@link Endpoint}.
     * This is used to 'circumvent' the design of the WebSocket API, which is usually annotation based but does not
     * support inheritance.
     *
     * @param sc The server container of this servlet.
     */
    private void addWebSocketEndpoint(ServerContainer sc) {
        try {
            sc.addEndpoint(ServerEndpointConfig.Builder.create(getClass(), getWebSocketUrl() + "/{clientId}").build());
        } catch (DeploymentException e) {
            throw new  IllegalStateException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // NO-OP
    }
}
