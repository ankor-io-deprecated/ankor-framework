package performance;

import at.irian.ankor.action.Action;
import at.irian.ankor.messaging.json.viewmodel.ViewModelJsonMessageMapper;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.servlet.websocket.messaging.WebSocketMessageBus;
import at.irian.ankor.servlet.websocket.session.WebSocketRemoteSystem;
import at.irian.ankor.session.SingletonSessionManager;
import at.irian.ankor.system.AnkorSystem;
import at.irian.ankor.system.AnkorSystemBuilder;

import javax.websocket.*;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PerformanceTest {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PerformanceTest.class);
    private static final String DEFAULT_SERVER = "wss://ankor-todo-sample.irian.at";
    // private static final String DEFAULT_SERVER = "ws://localhost:8080";
    private static final String DEFAULT_ENDPOINT = "/websocket/ankor";
    private static final int NUM_CONNECTIONS = 100;
    private static final int WAIT_MAX = 2000;
    private final ScheduledExecutorService pool = Executors.newScheduledThreadPool(10);
    int connectionCount = 0;
    private Random rand = new Random();

    public static void main(String[] args) {
        PerformanceTest t = new PerformanceTest();
        t.start();
    }

    private static Ref rootRefFor(AnkorSystem clientSystem) {
        return ((SingletonSessionManager) clientSystem.getSessionManager()).getSession().getRefContext().refFactory().ref("root");
    }

    public void start() {
        try {
            AnkorSystem clientSystem;
            for (int i = 0; i < NUM_CONNECTIONS; i++) {
                clientSystem = createWebSocketClientSystem(DEFAULT_SERVER, DEFAULT_ENDPOINT);

                Ref rootRef = rootRefFor(clientSystem);
                rootRef.fire(new Action("init"));

                RandomInteraction randomInteraction = new RandomInteraction(rootRef);
                pool.schedule(randomInteraction, randomDelay(), TimeUnit.MILLISECONDS);
            }
        } catch (Exception ex) {
            LOG.error("Something went wrong");
        }
    }

    private int randomDelay() {
        return rand.nextInt(WAIT_MAX);
    }

    private AnkorSystem createWebSocketClientSystem(String server, String endpoint) throws IOException, DeploymentException, InterruptedException {
        final String clientId = UUID.randomUUID().toString();

        final AnkorSystem[] clientSystem = new AnkorSystem[1];
        final WebSocketMessageBus messageBus = new WebSocketMessageBus(new ViewModelJsonMessageMapper());
        final AnkorSystemBuilder systemBuilder = new AnkorSystemBuilder()
                .withName(clientId)
                        //.withModelContextId("collabTest")
                .withMessageBus(messageBus);

        final CountDownLatch latch = new CountDownLatch(1);

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        String uri = server + endpoint + "/" + clientId;

        container.connectToServer(new Endpoint() {

            @Override
            public void onOpen(Session session, EndpointConfig config) {
                session.addMessageHandler(new MessageHandler.Whole<String>() {

                    @Override
                    public void onMessage(String message) {
                        messageBus.receiveSerializedMessage(message);
                    }
                });

                messageBus.addRemoteSystem(new WebSocketRemoteSystem(clientId, session));
                clientSystem[0] = systemBuilder.createClient();
                clientSystem[0].start();
                latch.countDown();
            }
        }, URI.create(uri));

        if (latch.await(5, TimeUnit.SECONDS)) {
            LOG.info("Client {} connected", ++connectionCount);
            return clientSystem[0];
        } else {
            throw new IOException("WebSocket could not connect to " + uri);
        }
    }

    class RandomInteraction implements Runnable {
        private Ref rootRef;

        public RandomInteraction(Ref rootRef) {
            this.rootRef = rootRef;
        }

        @Override
        public void run() {
            doRandomInteraction(rootRef);
            pool.schedule(this, randomDelay(), TimeUnit.MILLISECONDS);
        }

        protected void doRandomInteraction(Ref rootRef) {
            if (rand.nextBoolean()) {
                LOG.info("Add task");

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