package at.irian.ankor.system;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionEventListener;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.context.AnkorContext;
import at.irian.ankor.context.AnkorContextFactory;
import at.irian.ankor.event.ListenersHolder;
import at.irian.ankor.messaging.*;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.rmi.RemoteMethodActionEventListener;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class AnkorSystem {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorSystem.class);

    private final String name;
    private final MessageFactory messageFactory;
    private final MessageBus messageBus;
    private final ListenersHolder globalListenersHolder;
    private final AnkorContextFactory ankorContextFactory;
    private final RemoteMethodActionEventListener remoteMethodActionEventListener;
    private ChangeEventListener changeEventListener;
    private ActionEventListener actionEventListener;
    private MessageListener messageListener;

    protected AnkorSystem(String name,
                          MessageFactory messageFactory,
                          MessageBus messageBus,
                          ListenersHolder globalListenersHolder,
                          AnkorContextFactory ankorContextFactory,
                          RemoteMethodActionEventListener remoteMethodActionEventListener) {
        this.name = name;
        this.messageFactory = messageFactory;
        this.messageBus = messageBus;
        this.globalListenersHolder = globalListenersHolder;
        this.ankorContextFactory = ankorContextFactory;
        this.remoteMethodActionEventListener = remoteMethodActionEventListener;
    }

    public String getName() {
        return name;
    }

    public AnkorContextFactory getAnkorContextFactory() {
        return ankorContextFactory;
    }

    public MessageFactory getMessageFactory() {
        return messageFactory;
    }

    public MessageBus getMessageBus() {
        return messageBus;
    }

    public ListenersHolder getGlobalListenersHolder() {
        return globalListenersHolder;
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

        actionEventListener = new ActionEventListener(null) {
            @Override
            public void processAction(Ref actionProperty, Action action) {
                String modelContextPath = actionProperty.getRefContext().getModelContextPath();
                String actionPropertyPath = actionProperty.path();
                Message message = messageFactory.createActionMessage(modelContextPath, actionPropertyPath, action);
                AnkorContext.getCurrentInstance().getMessageSender().sendMessage(message);
            }
        };

        changeEventListener = new ChangeEventListener(null) {
            @Override
            public void processChange(Ref changedProperty) {
                Object newValue = changedProperty.getValue();
                String modelContextPath = changedProperty.getRefContext().getModelContextPath();
                String changedPropertyPath = changedProperty.path();
                Message message = messageFactory.createChangeMessage(modelContextPath, changedPropertyPath, newValue);
                AnkorContext.getCurrentInstance().getMessageSender().sendMessage(message);

                if (newValue == null) {
                    AnkorContext.getCurrentInstance().getModelHolder().getListenersHolder().cleanupListeners();
                }
            }
        };

        messageListener = new MessageListener() {
            @Override
            public void onActionMessage(ActionMessage message) {
                AnkorContext ankorContext = createAnkorContextFor(message);
                AnkorContext.setCurrentInstance(ankorContext);
                try {
                    Ref actionProperty = ankorContext.getRefFactory().ref(message.getActionPropertyPath());
                    if (message.getModelContextPath() != null) {
                        // if there is an explicit context in the message we use that, ...
                        actionProperty = actionProperty.withRefContext(actionProperty.getRefContext().withModelContextPath(message.getModelContextPath()));
                    } else {
                        // ... else we use the action source ref as the context of this action
                        actionProperty = actionProperty.withRefContext(actionProperty.getRefContext().withModelContextPath(message.getActionPropertyPath()));
                    }
                    actionProperty.fireAction(message.getAction());
                } finally {
                    ankorContext.getMessageSender().flush();
                    AnkorContext.setCurrentInstance(null);
                }
            }

            @Override
            public void onChangeMessage(ChangeMessage message) {
                AnkorContext ankorContext = createAnkorContextFor(message);
                AnkorContext.setCurrentInstance(ankorContext);
                try {
                    Ref changedProperty = ankorContext.getRefFactory().ref(message.getChange().getChangedProperty());
                    if (message.getModelContextPath() != null) {
                        changedProperty = changedProperty.withRefContext(changedProperty.getRefContext().withModelContextPath(message.getModelContextPath()));
                    }
                    if (changedProperty.isRoot() || changedProperty.isValid()) {
                        changedProperty.setValue(message.getChange().getNewValue());
                    }
                } finally {
                    ankorContext.getMessageSender().flush();
                    AnkorContext.setCurrentInstance(null);
                }
            }
        };

        globalListenersHolder.addListener(actionEventListener);
        globalListenersHolder.addListener(changeEventListener);
        messageBus.registerMessageListener(messageListener);

        if (remoteMethodActionEventListener != null) {
            globalListenersHolder.addListener(remoteMethodActionEventListener);
        }
    }

    private AnkorContext createAnkorContextFor(Message message) {
        AnkorContext ankorContext = ankorContextFactory.create();
        ReducingMessageSender reducingMessageSender = new ReducingMessageSender(ankorContext.getMessageSender(),
                                                                                ankorContext.getPathSyntax());
        CircuitBreakerMessageSender circuitBreaker
                = new CircuitBreakerMessageSender(reducingMessageSender, message);
        return ankorContext.withMessageSender(circuitBreaker);
    }


    @SuppressWarnings("UnusedDeclaration")
    public void stop() {

        LOG.info("Stopping {}", this);

        if (messageListener != null) {
            messageBus.unregisterMessageListener(messageListener);
            messageListener = null;
        }

        if (changeEventListener != null) {
            globalListenersHolder.removeListener(changeEventListener);
            changeEventListener = null;
        }

        if (actionEventListener != null) {
            globalListenersHolder.removeListener(actionEventListener);
            actionEventListener = null;
        }

        if (remoteMethodActionEventListener != null) {
            globalListenersHolder.removeListener(remoteMethodActionEventListener);
        }
    }

}
