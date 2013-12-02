package at.irian.ankor.websocket;

import at.irian.ankor.system.AnkorSystemBuilder;
import org.slf4j.Logger;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AnkorClientEndpoint extends Endpoint {
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorClientEndpoint.class);
    private static final long HEARTBEAT_INTERVAL = 5000;
    private AnkorSystemBuilder systemBuilder;
    private WebSocketMessageBus messageBus;
    private CountDownLatch latch;
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public AnkorClientEndpoint(AnkorSystemBuilder systemBuilder, WebSocketMessageBus messageBus, CountDownLatch latch) {
        this.systemBuilder = systemBuilder;
        this.messageBus = messageBus;
        this.latch = latch;
    }

    @Override
    public void onOpen(final Session session, EndpointConfig config) {

        session.addMessageHandler(new MessageHandler.Whole<String>() {
            private boolean idReceived = false;

            @Override
            public void onMessage(String message) {
                if (!idReceived) {
                    systemBuilder.withName(message);
                    messageBus.addRemoteSystem(new WebSocketRemoteSystem(message, session));
                    latch.countDown();
                    idReceived = true;
                } else {
                    messageBus.receiveSerializedMessage(message);
                }
            }
        });

        startHeartbeat(session);
        latch.countDown();
    }

    /**
     * Schedules a heartbeat message every fixed milliseconds.
     *
     * @param session The web socket {@link Session} used for sending the heartbeat messages.
     */
    private void startHeartbeat(final Session session) {
        LOG.info("Starting heartbeat");

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                session.getAsyncRemote().sendText(""); // heartbeat
            }
        }, HEARTBEAT_INTERVAL, HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);
    }
}
