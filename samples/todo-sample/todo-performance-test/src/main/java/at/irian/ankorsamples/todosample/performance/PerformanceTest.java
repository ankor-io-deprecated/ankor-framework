package at.irian.ankorsamples.todosample.performance;

import at.irian.ankor.action.Action;
import at.irian.ankor.ref.Ref;

import javax.websocket.*;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PerformanceTest {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PerformanceTest.class);
    private static final String DEFAULT_SERVER = "wss://ankor-todo-sample.irian.at";
    //private static final String DEFAULT_SERVER = "ws://localhost:8080";
    private static final String DEFAULT_ENDPOINT = "/websocket/ankor";
    private static final int NUM_CONNECTIONS = 500;
    private static final int WAIT_MAX = 2000;
    private static final long HEARTBEAT_INTERVAL = 5000;
    private static final long NEW_CONNECTION_INTERVAL = 500;
    private final ScheduledExecutorService pool = Executors.newScheduledThreadPool(200);
    private final ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
    private final AtomicInteger connectedClients = new AtomicInteger(0);
    private Random rand = new Random();

    public static void main(String[] args) {
        PerformanceTest t = new PerformanceTest();
        t.start();
    }

    public void start() {
        for (int i = 0; i < NUM_CONNECTIONS; i++) {
            final int ii = i;
            pool.schedule(new Runnable() {
                @Override
                public void run() {
                    try {
                        String uri = DEFAULT_SERVER + DEFAULT_ENDPOINT;
                        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                        LOG.debug("Connect to server, nr. {}", ii);
                        container.connectToServer(new DummyEndpoint(), URI.create(uri));
                    } catch (DeploymentException | IOException e) {
                        LOG.error("Retrying nr. {}", ii);
                        pool.schedule(this, NEW_CONNECTION_INTERVAL, TimeUnit.MILLISECONDS);
                    }
                }
            }, NEW_CONNECTION_INTERVAL * i, TimeUnit.MILLISECONDS);
        }
    }

    private int randomDelay() {
        return rand.nextInt(WAIT_MAX);
    }

    private class DummyEndpoint extends Endpoint {
        private String modelId;

        @Override
        public void onOpen(final Session session, EndpointConfig config) {

            modelId = Integer.toString(connectedClients.incrementAndGet());
            LOG.info("Number of connected clients: {}", modelId);

            session.addMessageHandler(new MessageHandler.Whole<String>() {

                public String id;
                private boolean idReceived = false;

                @Override
                public void onMessage(String message) {
                    LOG.debug("Received from server: {}", message);

                    if (!idReceived) {
                        idReceived = true;
                        id = message;

                        String s = "{\"senderId\":\"SENDER_ID\",\"modelId\":\"MODEL_ID\",\"messageId\":\"SENDER_ID#NUM\",\"property\":\"root\",\"action\":\"init\"}";
                        s = s.replaceAll("SENDER_ID", id);
                        s = s.replaceAll("MODEL_ID", modelId);
                        s = s.replace("NUM", Integer.toString(0));

                        try {
                            session.getBasicRemote().sendText(s);
                        } catch (IOException e) {
                            LOG.error("Error while sending init action");
                        }
                    }
                }
            });

            startHeartbeat(session);
        }

        @Override
        public void onError(Session session, Throwable thr) {
            LOG.debug("Connection closed because of an error {}", thr.getMessage());
            int i = connectedClients.decrementAndGet();
            LOG.info("Number of connected clients: {}", i);
        }

        @Override
        public void onClose(Session session, CloseReason closeReason) {
            LOG.info("Connection closed because of {}: {}", closeReason.getCloseCode(), closeReason.getReasonPhrase());
            int i = connectedClients.decrementAndGet();
            LOG.info("Number of connected clients: {}", i);
        }

        private void startHeartbeat(final Session session) {
            Runnable task = new Runnable() {

                @Override
                public void run() {
                    try {
                        session.getBasicRemote().sendText(""); // heartbeat
                    } catch (IOException e) {
                        LOG.error("Error while sending heartbeat");
                    }
                }
            };

            timer.scheduleAtFixedRate(task, HEARTBEAT_INTERVAL, HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);
        }
    }

    class RandomInteraction implements Runnable {
        private Ref rootRef;

        public RandomInteraction(Ref rootRef) {
            this.rootRef = rootRef;
        }

        @Override
        public void run() {
            // doRandomInteraction(rootRef);
            pool.schedule(this, randomDelay(), TimeUnit.MILLISECONDS);
        }

        protected void doRandomInteraction(Ref rootRef) {
            if (rand.nextBoolean()) {
                LOG.info("Add task {}");

                Map<String, Object> params = new HashMap<>();
                params.put("title", randomString());
                rootRef.fire(new Action("newTask", params));
            } else {
                LOG.info("Remove task");

                int numTasks = rootRef.appendPath("model.itemsLeft").getValue();
                if (numTasks > 0) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("index", 0);
                    rootRef.appendPath("model").fire(new Action("deleteTask", params));
                }
            }
        }

        private String randomString() {
            return (new BigInteger(rand.nextInt(160), rand).toString(32));
        }
    }
}