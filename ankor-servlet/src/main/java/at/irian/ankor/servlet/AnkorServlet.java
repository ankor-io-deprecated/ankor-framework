package at.irian.ankor.servlet;

import at.irian.ankor.context.ModelContext;
import at.irian.ankor.event.EventListeners;
import at.irian.ankor.messaging.Message;
import at.irian.ankor.messaging.json.viewmodel.ViewModelJsonMessageMapper;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.system.AnkorSystem;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        String clientId = null;
        Set<String> modelIds = new HashSet<String>();
        for (Message message : messages) {
            if (clientId == null) {
                clientId = message.getSenderId();
            } else if (!clientId.equals(message.getSenderId())) {
                throw new IllegalStateException("Different client ids in one request?!");
            }
            modelIds.add(message.getModelId());
            messageBus.receiveMessage(message);
        }

        // send (response) messages
        if (clientId != null) {

            // queue special event for signalling that server finished the event processing
            final Set<String> finishedModelRequests = new HashSet<String>(modelIds);
            for (final String modelId : modelIds) {

                ModelContext modelContext = ankorSystem.getModelContextManager().getOrCreate(modelId);
                final EventListeners eventListeners = modelContext.getEventListeners();
                eventListeners.add(new RequestFinishedEvent.Listener() {
                    @Override
                    public boolean isDiscardable() {
                        return false;
                    }

                    @Override
                    public void processRequestFinished(RequestFinishedEvent requestFinishedEvent) {
                        synchronized (finishedModelRequests) {
                            finishedModelRequests.remove(modelId);
                            finishedModelRequests.notifyAll();
                        }
                        eventListeners.remove(this);
                    }
                });

                Ref rootRef = ankorSystem.getRefContextFactory().createRefContextFor(modelContext).refFactory().rootRef();
                modelContext.getEventDispatcher().dispatch(new RequestFinishedEvent(rootRef));
            }

            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (finishedModelRequests) {
                while (!finishedModelRequests.isEmpty()) {
                    try {
                        LOG.info("Waiting for finished events for models {}", finishedModelRequests);
                        finishedModelRequests.wait();
                    } catch (InterruptedException e) {
                        Thread.interrupted();
                    }
                }
            }

            // send all response messages
            List<Message> pendingMessages = messageBus.getAndClearPendingMessagesFor(clientId);
            Message[] messagesToSend = pendingMessages.toArray(new Message[pendingMessages.size()]);
            String responseMessages = messageMapper.serializeArray(messagesToSend);
            resp.setCharacterEncoding("UTF-8");
            PrintWriter writer = resp.getWriter();
            writer.write(responseMessages);
            writer.flush();
        }

    }

}
