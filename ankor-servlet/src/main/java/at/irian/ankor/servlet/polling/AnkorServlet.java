package at.irian.ankor.servlet.polling;

import at.irian.ankor.session.ModelSession;
import at.irian.ankor.event.EventListeners;
import at.irian.ankor.event.source.CustomSource;
import at.irian.ankor.messaging.Message;
import at.irian.ankor.messaging.json.viewmodel.ViewModelJsonMessageMapper;
import at.irian.ankor.system.AnkorSystem;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Manfred Geiler
 */
public class AnkorServlet extends HttpServlet {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorServlet.class);

    private AnkorSystem ankorSystem;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ankorSystem = AnkorServletContextListener.getAnkorSystem(config.getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletMessageBus messageBus = (ServletMessageBus)ankorSystem.getMessageBus();
        ViewModelJsonMessageMapper messageMapper = messageBus.getMessageMapper();

        // receive messages
        String serializedMessages = req.getParameter("messages");
        Message[] messages = messageMapper.deserializeArray(serializedMessages);
        String clientId = req.getParameter("clientId");
        Set<String> requestModelIds = new HashSet<String>();
        for (Message message : messages) {
            if (clientId == null) {
                clientId = message.getSenderId();
            } else if (!clientId.equals(message.getSenderId())) {
                throw new IllegalStateException("Different client ids in one request?!");
            }
            requestModelIds.add(message.getModelId());
            LOG.debug("received: {}", message);
            messageBus.receiveMessage(message);
        }

        // send (response) messages
        if (clientId != null) {

            // wait for incoming messages being processed
            waitForMessagesProcessed(requestModelIds);

            // send all response messages
            Collection<Message> pendingMessages = messageBus.getPendingMessagesFor(clientId);
            Message[] messagesToSend = pendingMessages.toArray(new Message[pendingMessages.size()]);
            String responseMessages = messageMapper.serializeArray(messagesToSend);
            resp.setCharacterEncoding("UTF-8");
            PrintWriter writer = resp.getWriter();
            writer.write(responseMessages);
            writer.flush();
        }

    }

    private void waitForMessagesProcessed(Set<String> requestedModelIds) {

        if (requestedModelIds.isEmpty()) {
            return;
        }

        // queue special event for signalling that server finished the event processing for each model
        final BlockingQueue<String> finishedModelRequests = new LinkedBlockingQueue<String>();
        for (final String modelId : requestedModelIds) {

            ModelSession modelSession = ankorSystem.getModelSessionManager().getOrCreate(modelId);
            final EventListeners eventListeners = modelSession.getEventListeners();
            eventListeners.add(new RequestFinishedEvent.Listener() {
                @Override
                public boolean isDiscardable() {
                    return false;
                }

                @Override
                public void processRequestFinished(RequestFinishedEvent requestFinishedEvent) {
                    finishedModelRequests.add(modelId);
                    eventListeners.remove(this);
                }
            });

            modelSession.getEventDispatcher().dispatch(new RequestFinishedEvent(new CustomSource(this)));
        }

        boolean interrupted = false;

        Set<String> outstandingModelIds = new HashSet<String>(requestedModelIds);
        while (!outstandingModelIds.isEmpty()) {
            try {
                String finishedModelId = finishedModelRequests.take();
                if (!outstandingModelIds.remove(finishedModelId)) {
                    throw new IllegalStateException("not waiting for " + finishedModelId);
                }
            } catch (InterruptedException e) {
                interrupted = true;
            }
        }

        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }

}
