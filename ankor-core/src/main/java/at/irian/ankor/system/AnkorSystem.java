package at.irian.ankor.system;

import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.change.ChangeEvent;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.event.EventListeners;
import at.irian.ankor.messaging.*;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefContextFactory;
import at.irian.ankor.rmi.RemoteMethodActionEventListener;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class AnkorSystem {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorSystem.class);

    private final String name;
    private final MessageFactory messageFactory;
    private final MessageBus messageBus;
    private final EventListeners globalEventListeners;
    private final RefContextFactory refContextFactory;
    private final RemoteMethodActionEventListener remoteMethodActionEventListener;
    private ChangeEventListener changeEventListener;
    private ActionEvent.Listener actionEventListener;
    private MessageListener messageListener;

    protected AnkorSystem(String name,
                          MessageFactory messageFactory,
                          MessageBus messageBus,
                          EventListeners globalEventListeners,
                          RefContextFactory refContextFactory,
                          RemoteMethodActionEventListener remoteMethodActionEventListener) {
        this.name = name;
        this.messageFactory = messageFactory;
        this.messageBus = messageBus;
        this.globalEventListeners = globalEventListeners;
        this.refContextFactory = refContextFactory;
        this.remoteMethodActionEventListener = remoteMethodActionEventListener;
    }

    public String getName() {
        return name;
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
        return "AnkorSystem{'" + name + "'}";
    }

    public boolean isStarted() {
        return actionEventListener != null || changeEventListener != null || messageListener != null;
    }

    public void start() {

        LOG.info("Starting {}", this);

        if (isStarted()) {
            throw new IllegalStateException("already started?");
        }

        actionEventListener = new ActionEvent.Listener(null) {
            @Override
            public void process(ActionEvent event) {
                Ref actionProperty = event.getActionProperty();
                String modelContextPath = actionProperty.context().getModelContextPath();
                String actionPropertyPath = actionProperty.path();
                Message message = messageFactory.createActionMessage(modelContextPath,
                                                                     actionPropertyPath,
                                                                     event.getAction());
                actionProperty.context().messageSender().sendMessage(message);
            }
        };

        changeEventListener = new ChangeEventListener(null) {
            @Override
            public void process(ChangeEvent event) {
                Ref changedProperty = event.getChangedProperty();
                Object newValue = changedProperty.getValue();
                RefContext refContext = changedProperty.context();
                String modelContextPath = refContext.getModelContextPath();
                String changedPropertyPath = changedProperty.path();

                Message message = messageFactory.createChangeMessage(modelContextPath, changedPropertyPath, newValue);
                refContext.messageSender().sendMessage(message);

                if (newValue == null) {
                    refContext.eventListeners().cleanup();
                }
            }
        };

        messageListener = new MessageListener() {
            @Override
            public void onActionMessage(ActionMessage message) {
                RefContext initialRefContext = createRefContextFor(message);
                Ref actionProperty = initialRefContext.refFactory().ref(message.getActionPropertyPath());
                if (message.getModelContextPath() != null) {
                    // if there is an explicit context in the message we use that, ...
                    actionProperty = actionProperty.withRefContext(actionProperty.context().withModelContextPath(message.getModelContextPath()));
                } else {
                    // ... else we use the action source ref as the context of this action
                    actionProperty = actionProperty.withRefContext(actionProperty.context().withModelContextPath(message.getActionPropertyPath()));
                }
                actionProperty.fireAction(message.getAction());
                initialRefContext.messageSender().flush();
            }

            @Override
            public void onChangeMessage(ChangeMessage message) {
                RefContext initialRefContext = createRefContextFor(message);
                Ref changedProperty = initialRefContext.refFactory().ref(message.getChange().getChangedProperty());
                if (message.getModelContextPath() != null) {
                    changedProperty = changedProperty.withRefContext(changedProperty.context().withModelContextPath(message.getModelContextPath()));
                }
                if (changedProperty.isRoot() || changedProperty.isValid()) {
                    changedProperty.setValue(message.getChange().getNewValue());
                }
                initialRefContext.messageSender().flush();
            }
        };

        globalEventListeners.add(actionEventListener);
        globalEventListeners.add(changeEventListener);
        messageBus.registerMessageListener(messageListener);

        if (remoteMethodActionEventListener != null) {
            globalEventListeners.add(remoteMethodActionEventListener);
        }
    }

    public RefContext createInitialRefContext() {
        return refContextFactory.create();
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

        if (remoteMethodActionEventListener != null) {
            globalEventListeners.remove(remoteMethodActionEventListener);
        }
    }

}
