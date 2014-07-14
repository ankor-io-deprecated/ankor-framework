package at.irian.ankorsamples.todosample.fxclient.starter;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Thomas Spiegl
 */
public class TodoWebSocketStressTest {

    private static AtomicInteger clientCount = new AtomicInteger(0);
    private static AtomicInteger doneCount = new AtomicInteger(0);

    private static int POOL_SIZE = 15;
    private static int CLIENTS = 500;

    private static long STARTED_TIME = System.currentTimeMillis();

    public static void main(String ... args) {
        ForkJoinPool pool = new ForkJoinPool(POOL_SIZE);
        pool.execute(new ConnectTask(CLIENTS));
        try {
            Thread.sleep(1000 * 500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class ConnectTask extends ForkJoinTask<Object> {

        private final int count;

        ConnectTask(int count) {
            this.count = count;
        }

        @Override
        public Object getRawResult() {
            return null;
        }

        @Override
        protected void setRawResult(Object value) {
        }

        @Override
        protected boolean exec() {
            if (count == 1) {
                synchronized(clientCount) {
                    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                    try {
                        String clientId = clientCount.incrementAndGet() + "-" + System.currentTimeMillis();
                        container.connectToServer(
                                new TestEndpoint(clientId),
                                URI.create("ws://localhost:8080/websocket/ankor/" + clientId));
                    } catch (DeploymentException | IOException e) {
                        e.printStackTrace();
                        throw new IllegalStateException(e);
                    }
                }
            } else {
                List<ConnectTask> tasks = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    tasks.add(new ConnectTask(1));
                }
                invokeAll(tasks);
            }
            return true;
        }
    }

    static class TestEndpoint extends Endpoint implements MessageHandler.Whole<String> {
        private String clientId;
        private Session session;
        private final AtomicBoolean sendTasks = new AtomicBoolean(true);

        TestEndpoint(String clientId) {
            this.clientId = clientId;
        }

        @Override
        public void onOpen(Session session, EndpointConfig config) {
            this.session = session;
            this.session.addMessageHandler(this);
            send(session, "{\"property\":\"root\", \"connectParams\":{\"todoListId\":\"collaborationTest\"}}");
        }

        @Override
        public void onMessage(String message) {
            if (sendTasks.get()) {
                send(session, "{\"property\":\"root.model\",\"action\":{\"name\":\"newTask\",\"params\":{\"title\":\"task-" + clientId + "\"}}}");
                sendTasks.set(false);
                done();
            }
        }

        private void done() {
            int done = doneCount.incrementAndGet();
            if (done >= CLIENTS) {
                System.out.println("DONE ------------ " + ((System.currentTimeMillis() - STARTED_TIME) / 1000));
            } else if (done % 10 == 0) {
                System.out.println("done " + done + " " + ((System.currentTimeMillis() - STARTED_TIME) / 1000));
            }
        }

        @Override
        public void onError(Session session, Throwable thr) {
            done();
            System.out.println("error " + clientId + " " + thr.getMessage());
        }

        @Override
        public void onClose(Session session, CloseReason closeReason) {
            done();
            System.out.println("closed " + clientId + " " + closeReason);
        }

        private void send(Session session, String connectMsg) {
            try {
                session.getBasicRemote().sendText(connectMsg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
