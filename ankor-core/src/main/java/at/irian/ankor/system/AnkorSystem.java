package at.irian.ankor.system;

import at.irian.ankor.messaging.*;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefContextFactory;
import at.irian.ankor.session.Session;
import at.irian.ankor.session.SessionManager;

/**
 * @author Manfred Geiler
 */
public class AnkorSystem {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorSystem.class);

    private final String systemName;
    private final MessageFactory messageFactory;
    private final MessageBus messageBus;
    private final RefContextFactory refContextFactory;
    private final SessionManager sessionManager;
    private MessageListener messageListener;

    protected AnkorSystem(String systemName,
                          MessageFactory messageFactory,
                          MessageBus messageBus,
                          RefContextFactory refContextFactory,
                          SessionManager sessionManager) {
        this.systemName = systemName;
        this.messageFactory = messageFactory;
        this.messageBus = messageBus;
        this.refContextFactory = refContextFactory;
        this.sessionManager = sessionManager;
    }

    public String getSystemName() {
        return systemName;
    }

    @Deprecated
    public MessageFactory getMessageFactory() {
        return messageFactory;
    }

    public MessageBus getMessageBus() {
        return messageBus;
    }

    public RefContextFactory getRefContextFactory() {
        return refContextFactory;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    @Override
    public String toString() {
        return "AnkorSystem{'" + systemName + "'}";
    }

    public boolean isStarted() {
        return messageListener != null;
    }

    public void start() {

        LOG.info("Starting {}", this);

        if (isStarted()) {
            throw new IllegalStateException("already started?");
        }

        messageListener = new MessageListener() {
            @Override
            public void onActionMessage(ActionMessage message) {
                String sessionId = message.getSessionId();
                Session session = sessionManager.getOrCreateSession(sessionId);

                MessageSender originalMessageSender = session.getMessageSender();
                MessageSender messageSender = new CircuitBreakerMessageSender(messageBus, message);
                session.setMessageSender(messageSender);

                if (!session.isActive()) {
                    session.start();
                }

                RefContext refContext = session.getRefContext();
                Ref actionProperty = refContext.refFactory().ref(message.getActionPropertyPath());
                actionProperty.fireAction(message.getAction());

                messageSender.flush();
                session.setMessageSender(originalMessageSender);
            }

            @Override
            public void onChangeMessage(ChangeMessage message) {
                String sessionId = message.getSessionId();
                Session session = sessionManager.getOrCreateSession(sessionId);

                MessageSender originalMessageSender = session.getMessageSender();
                MessageSender messageSender = new CircuitBreakerMessageSender(messageBus, message);
                session.setMessageSender(messageSender);

                if (!session.isActive()) {
                    session.start();
                }

                RefContext refContext = session.getRefContext();
                Ref changedProperty = refContext.refFactory().ref(message.getChange().getChangedProperty());
                if (changedProperty.isRoot() || changedProperty.isValid()) {
                    changedProperty.setValue(message.getChange().getNewValue());
                }

                messageSender.flush();
                session.setMessageSender(originalMessageSender);
            }
        };

        messageBus.registerMessageListener(messageListener);
    }


    @SuppressWarnings("UnusedDeclaration")
    public void stop() {

        LOG.info("Stopping {}", this);

        if (messageListener != null) {
            messageBus.unregisterMessageListener(messageListener);
            messageListener = null;
        }

    }

}
