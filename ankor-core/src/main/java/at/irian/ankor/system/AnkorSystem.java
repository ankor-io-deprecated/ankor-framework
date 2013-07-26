package at.irian.ankor.system;

import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.change.ChangeEvent;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.event.EventListeners;
import at.irian.ankor.messaging.*;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefContextFactory;

/**
 * @author Manfred Geiler
 */
public class AnkorSystem {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorSystem.class);

    private final String systemName;
    private final MessageFactory messageFactory;
    private final MessageBus messageBus;
    private final EventListeners globalEventListeners;
    private final RefContextFactory refContextFactory;
    private ChangeEventListener changeEventListener;
    private ActionEvent.Listener actionEventListener;
    private MessageListener messageListener;

    protected AnkorSystem(String systemName,
                          MessageFactory messageFactory,
                          MessageBus messageBus,
                          EventListeners globalEventListeners,
                          RefContextFactory refContextFactory) {
        this.systemName = systemName;
        this.messageFactory = messageFactory;
        this.messageBus = messageBus;
        this.globalEventListeners = globalEventListeners;
        this.refContextFactory = refContextFactory;
    }

    public String getSystemName() {
        return systemName;
    }

    public MessageFactory getMessageFactory() {
        return messageFactory;
    }

    public MessageBus getMessageBus() {
        return messageBus;
    }

    public EventListeners getGlobalEventListeners() {
        return globalEventListeners;
    }

    public RefContextFactory getRefContextFactory() {
        return refContextFactory;
    }

    @Override
    public String toString() {
        return "AnkorSystem{'" + systemName + "'}";
    }

    public boolean isStarted() {
        return actionEventListener != null || changeEventListener != null || messageListener != null;
    }

    public void start() {

        LOG.info("Starting {}", this);

        if (isStarted()) {
            throw new IllegalStateException("already started?");
        }

        // global action event listener for sending action events to remote partner
        actionEventListener = new ActionEvent.Listener(null) {
            @Override
            public void process(ActionEvent event) {
                Ref actionProperty = event.getActionProperty();
                String actionPropertyPath = actionProperty.path();
                Message message = messageFactory.createActionMessage(actionPropertyPath, event.getAction());
                actionProperty.context().messageSender().sendMessage(message);
            }
        };

        // global change event listener for sending change events to remote partner
        changeEventListener = new ChangeEventListener(null) {
            @Override
            public void process(ChangeEvent event) {
                Ref changedProperty = event.getChangedProperty();
                Object newValue = changedProperty.getValue();
                RefContext refContext = changedProperty.context();
                String changedPropertyPath = changedProperty.path();
                Message message = messageFactory.createChangeMessage(changedPropertyPath, newValue);
                refContext.messageSender().sendMessage(message);

                // in addition to sending change events to remote, cleanup orphaned listeners
                if (newValue == null) {
                    refContext.eventListeners().cleanup();
                }
            }
        };

        globalEventListeners.add(actionEventListener);
        globalEventListeners.add(changeEventListener);

        messageListener = new MessageListener() {
            @Override
            public void onActionMessage(ActionMessage message) {
                RefContext initialRefContext = createRefContextFor(message);
                Ref actionProperty = initialRefContext.refFactory().ref(message.getActionPropertyPath());
                actionProperty.fireAction(message.getAction());
                initialRefContext.messageSender().flush();
            }

            @Override
            public void onChangeMessage(ChangeMessage message) {
                RefContext initialRefContext = createRefContextFor(message);
                Ref changedProperty = initialRefContext.refFactory().ref(message.getChange().getChangedProperty());
                if (changedProperty.isRoot() || changedProperty.isValid()) {
                    changedProperty.setValue(message.getChange().getNewValue());
                }
                initialRefContext.messageSender().flush();
            }
        };

        messageBus.registerMessageListener(messageListener);
    }

    public RefContext createInitialRefContext() {
        return refContextFactory.createRefContext();
    }

    private RefContext createRefContextFor(Message message) {
        RefContext initialRefContext = createInitialRefContext();
//        ReducingMessageSender reducingMessageSender = new ReducingMessageSender(initialRefContext.messageSender(),
//                                                                                initialRefContext.pathSyntax());
        CircuitBreakerMessageSender circuitBreaker
                = new CircuitBreakerMessageSender(initialRefContext.messageSender(), message);
        return initialRefContext.withMessageSender(circuitBreaker);
    }


    @SuppressWarnings("UnusedDeclaration")
    public void stop() {

        LOG.info("Stopping {}", this);

        if (messageListener != null) {
            messageBus.unregisterMessageListener(messageListener);
            messageListener = null;
        }

        if (changeEventListener != null) {
            globalEventListeners.remove(changeEventListener);
            changeEventListener = null;
        }

        if (actionEventListener != null) {
            globalEventListeners.remove(actionEventListener);
            actionEventListener = null;
        }
    }

}
