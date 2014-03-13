package at.irian.ankor.http;

import at.irian.ankor.messaging.AbstractMessageLoop;
import at.irian.ankor.messaging.Message;
import at.irian.ankor.messaging.PassThroughMessageMapper;
import at.irian.ankor.messaging.json.simpletree.SimpleTreeJsonMessageMapper;
import at.irian.ankor.connection.RemoteSystem;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author Manfred Geiler
 */
@Deprecated
public class ClientHttpMessageLoop extends AbstractMessageLoop<Message> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ClientHttpMessageLoop.class);

    private final ServerHost serverHost;
    private final HttpClient httpClient;
    private final SimpleTreeJsonMessageMapper jsonMessageMapper;
    private final ScheduledExecutorService scheduledExecutorService;
    private final Queue<Message> outgoingMessages = new LinkedBlockingQueue<Message>();
    private final BlockingQueue<Message> incomingMessages = new LinkedBlockingQueue<Message>();
    private ScheduledFuture<?> scheduledFuture;

    public ClientHttpMessageLoop(String clientName, ServerHost serverHost) {
        super(clientName, new PassThroughMessageMapper());
        this.serverHost = serverHost;
        this.httpClient = new DefaultHttpClient();
        this.jsonMessageMapper = new SimpleTreeJsonMessageMapper();
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void start(boolean daemon) {
        super.start(daemon);
        scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    sendPendingMessages();
                } catch (Exception e) {
                    LOG.error("error sending messages to " + serverHost, e);
                }
            }
        }, 100, 100, TimeUnit.MILLISECONDS);
    }

    private void sendPendingMessages() throws IOException {
        List<Message> messagesToSend = null;

        Message poll = outgoingMessages.poll();
        while (poll != null) {
            if (messagesToSend == null) {
                messagesToSend = new ArrayList<Message>();
            }
            messagesToSend.add(poll);
            poll = outgoingMessages.poll();
        }
        if (messagesToSend == null) {
            messagesToSend = Collections.emptyList();
        }

        Message[] messages = messagesToSend.toArray(new Message[messagesToSend.size()]);
        String serializedMessages = jsonMessageMapper.serializeArray(messages);

        HttpPost httpPost = new HttpPost(serverHost.getUrl());
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("messages", serializedMessages));
        nvps.add(new BasicNameValuePair("clientId", name));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        HttpResponse response = httpClient.execute(httpPost);

        try {
            HttpEntity entity = response.getEntity();
            String serializedResponse = EntityUtils.toString(entity);
            Message[] responseMessages = jsonMessageMapper.deserializeArray(serializedResponse);
            Collections.addAll(incomingMessages, responseMessages);
        } finally {
            httpPost.releaseConnection();
        }
    }

    @Override
    public void stop() {
        super.stop();
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
    }

    @Override
    protected Collection<? extends RemoteSystem> getKnownRemoteSystems() {
        return Collections.singleton(serverHost);
    }

    @Override
    protected void send(String remoteSystemId, Message msg) {
        outgoingMessages.add(msg);
    }

    @Override
    protected Message receive() throws InterruptedException {
        return incomingMessages.take();
    }

}
