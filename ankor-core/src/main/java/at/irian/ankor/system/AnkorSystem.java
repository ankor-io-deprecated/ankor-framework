package at.irian.ankor.system;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionEventListener;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.context.AnkorContext;
import at.irian.ankor.context.AnkorContextFactory;
import at.irian.ankor.event.EventBus;
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
    private final EventBus globalEventBus;
    private final AnkorContextFactory ankorContextFactory;
    private final RemoteMethodActionEventListener remoteMethodActionEventListener;
    private ChangeEventListener changeEventListener;
    private ActionEventListener actionEventListener;
    private MessageListener messageListener;

    protected AnkorSystem(String name,
                          MessageFactory messageFactory,
                          MessageBus messageBus,
                          EventBus globalEventBus,
                          AnkorContextFactory ankorContextFactory,
                          RemoteMethodActionEventListener remoteMethodActionEventListener) {
        this.name = name;
        this.messageFactory = messageFactory;
        this.messageBus = messageBus;
        this.globalEventBus = globalEventBus;
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

    public EventBus getGlobalEventBus() {
        return globalEventBus;
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
                Message msg = AnkorContext.getCurrentInstance().getCurrentRemoteMessage();
                if (msg instanceof ActionMessage) {
                    ActionMessage actionMessage = (ActionMessage) msg;
                    if (actionMessage.getActionPropertyPath().equals(actionProperty.path())
                        && actionMessage.getAction() == action) {
                        // this action was caused by a remote action message, must not relay it back
                        return;
                    }
                }

                String modelContextPath = actionProperty.getRefContext().getModelContextPath();
                String actionPropertyPath = actionProperty.path();
                Message message = messageFactory.createActionMessage(modelContextPath, actionPropertyPath, action);
                messageBus.sendMessage(message);
            }
        };

        changeEventListener = new ChangeEventListener(null) {
            @Override
            public void processChange(Ref changedProperty) {
                Object newValue = changedProperty.getValue();

                Message msg = AnkorContext.getCurrentInstance().getCurrentRemoteMessage();
                if (msg instanceof ChangeMessage) {
                    ChangeMessage changeMessage = (ChangeMessage) msg;
                    if (changeMessage.getChange().getChangedProperty().equals(changedProperty.path())
                        && changeMessage.getChange().getNewValue() == newValue) {
                        // this change event was caused by a remote change message, must not relay it back
                        return;
                    }
                }

                String modelContextPath = changedProperty.getRefContext().getModelContextPath();
                String changedPropertyPath = changedProperty.path();
                Message message = messageFactory.createChangeMessage(modelContextPath, changedPropertyPath, newValue);
                messageBus.sendMessage(message);

                if (newValue == null) {
                    AnkorContext.getCurrentInstance().getModelHolder().getEventBus().cleanupListeners();
                }
            }
        };

        messageListener = new MessageListener() {
            @Override
            public void onActionMessage(ActionMessage message) {
                AnkorContext ankorContext = ankorContextFactory.create();
                ankorContext.setCurrentRemoteMessage(message);
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
                    ankorContext.setCurrentRemoteMessage(null);
                    AnkorContext.setCurrentInstance(null);
                }
            }

            @Override
            public void onChangeMessage(ChangeMessage message) {
                AnkorContext ankorContext = ankorContextFactory.create();
                ankorContext.setCurrentRemoteMessage(message);
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
                    ankorContext.setCurrentRemoteMessage(null);
                    AnkorContext.setCurrentInstance(null);
                }
            }
        };

        globalEventBus.addListener(actionEventListener);
        globalEventBus.addListener(changeEventListener);
        messageBus.registerMessageListener(messageListener);

        if (remoteMethodActionEventListener != null) {
            globalEventBus.addListener(remoteMethodActionEventListener);
        }
    }


    @SuppressWarnings("UnusedDeclaration")
    public void stop() {

        LOG.info("Stopping {}", this);

        if (messageListener != null) {
            messageBus.unregisterMessageListener(messageListener);
            messageListener = null;
        }

        if (changeEventListener != null) {
            globalEventBus.removeListener(changeEventListener);
            changeEventListener = null;
        }

        if (actionEventListener != null) {
            globalEventBus.removeListener(actionEventListener);
            actionEventListener = null;
        }

        if (remoteMethodActionEventListener != null) {
            globalEventBus.removeListener(remoteMethodActionEventListener);
        }
    }

}
