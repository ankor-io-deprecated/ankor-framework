package at.irian.ankorsamples.todosample.servlet;

import at.irian.ankor.session.RemoteSystem;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.VoidAckCallback;
import com.corundumstudio.socketio.misc.ConcurrentHashSet;

public class SocketIORemoteSystem implements RemoteSystem {

    private String id;
    private SocketIOClient client;

    private ConcurrentHashSet<String> pendingMessages = new ConcurrentHashSet<>();

    public SocketIORemoteSystem(SocketIOClient resource) {
        this.id = resource.getSessionId().toString();
        this.client = resource;
    }

    @Override
    public String getId() {
        return id;
    }

    public void sendMessage(String msg) {
        switch (client.getTransport()) {
            case XHRPOLLING:
                pendingMessages.add(msg);

                // XXX: Waiting for other messages to bundle up, since consecutive writes do not succeed with long polling
                try { Thread.sleep(25); } catch (InterruptedException ignored) {}

                String bundle = "";
                for (String s : pendingMessages) {
                    pendingMessages.remove(s);
                    bundle += s + "\n";
                }

                pendingMessages.add(bundle);
                client.sendMessage(bundle, new RetryAckCallback(bundle, 3));
                break;

            default:
                pendingMessages.add(msg);
                client.sendMessage(msg, new RetryAckCallback(msg, 1));
        }
    }

    private class RetryAckCallback extends VoidAckCallback {

        private final String pendingMessage;
        private int numRetries;

        public RetryAckCallback(String pendingMessage, int numRetries) {
            super(1);
            this.pendingMessage = pendingMessage;
            this.numRetries = numRetries;
        }

        @Override
        protected void onSuccess() {
            pendingMessages.remove(pendingMessage);
        }

        @Override
        public void onTimeout() {
            if (pendingMessages.contains(pendingMessage)) {
                if (numRetries > 0) {
                    client.sendMessage(pendingMessage, new RetryAckCallback(pendingMessage, numRetries - 1));
                } else {
                    throw new RuntimeException("Unsuccessfully retried sending a message");
                }
            }
        }
    }
}
