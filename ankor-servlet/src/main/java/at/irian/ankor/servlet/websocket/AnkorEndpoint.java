package at.irian.ankor.servlet.websocket;

import at.irian.ankor.akka.AnkorActorSystem;
import at.irian.ankor.base.BeanResolver;
import at.irian.ankor.delay.AkkaScheduler;
import at.irian.ankor.event.dispatch.AkkaEventDispatcherFactory;
import at.irian.ankor.messaging.json.viewmodel.ViewModelJsonMessageMapper;
import at.irian.ankor.session.ModelRootFactory;
import at.irian.ankor.system.AnkorSystem;
import at.irian.ankor.system.AnkorSystemBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import java.io.IOException;
import java.util.UUID;

/**
 * This is the base class of a WebSocket endpoint that communicates with a {@link AnkorSystem}.
 * It is meant reside on a web server (e.g. GlassFish) that supports JSR 365 (javax.websocket).
 *
 * This class handles new WebSocket connections on a fixed url ("/websockets/ankor"), which is also used by the JS
 * clients WebSocketTransport class. It registers the connection with the AnkorSystem, so that outgoing messages will
 * be sent the the clients and passes incoming messages to the AnkorSystem.
 *
 * As a user you need to subtype this class and override a couple of methods, most notably the
 * {@link #getModelRootFactory()}. This factory provides the base level model, which is unique to your application.
 *
 * Due to the design of the WebSocket API you have to subtype {@link AnkorWebSocketConfig} as well.
 *
 * @see AnkorWebSocketConfig
 * @author Florian Klampfer
 */
// TODO: Actual session management via ping-pongs or timeouts, etc...
public abstract class AnkorEndpoint extends Endpoint implements MessageHandler.Whole<String> {

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
        clientId = UUID.randomUUID().toString();
        LOG.info(" New client connected {}", clientId);

        session.addMessageHandler(this);
        webSocketMessageBus.addRemoteSystem(new WebSocketRemoteSystem(clientId, session));

        try {
            session.getBasicRemote().sendText(clientId);
        } catch (IOException e) {
            LOG.error(e.toString());
        }
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
    protected abstract ModelRootFactory getModelRootFactory();
}
