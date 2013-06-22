package at.irian.ankor.service;

import at.irian.ankor.action.Action;
import at.irian.ankor.application.Application;
import at.irian.ankor.application.AutoUnregisterChangeListener;
import at.irian.ankor.application.BoundChangeListener;
import at.irian.ankor.application.ListenerRegistry;
import at.irian.ankor.event.ActionListener;
import at.irian.ankor.event.ChangeListener;
import at.irian.ankor.messaging.*;
import at.irian.ankor.ref.Ref;

import java.util.Collection;

/**
 * @author MGeiler (Manfred Geiler)
 */
public abstract class AnkorServerBase implements MessageListener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorServerBase.class);

    private final Application application;
    private final MessageFactory messageFactory;
    private final MessageBus messageBus;

    public AnkorServerBase(Application application, MessageFactory messageFactory, MessageBus messageBus) {
        this.application = application;
        this.messageFactory = messageFactory;
        this.messageBus = messageBus;
    }

    public void start() {
        LOG.debug("Starting {}", this);

        ListenerRegistry listenerRegistry = application.getListenerRegistry();

        listenerRegistry.registerLocalChangeListener(null, new LocalChangeListener());
        listenerRegistry.registerLocalActionListener(null, new LocalActionListener());

        AutoUnregisterChangeListener autoUnregisterChangeListener = new AutoUnregisterChangeListener(listenerRegistry);
        listenerRegistry.registerLocalChangeListener(null, autoUnregisterChangeListener);
        listenerRegistry.registerRemoteChangeListener(null, autoUnregisterChangeListener);

        messageBus.registerMessageListener(this);
    }

    public void stop() {
        LOG.debug("Stopping {}", this);
        messageBus.unregisterMessageListener(this);
        application.getListenerRegistry().unregisterAllListeners();
    }


    protected Application getApplication() {
        return application;
    }

    public MessageBus getMessageBus() {
        return messageBus;
    }

    protected void receiveAction(Ref modelContext, Action action) {
        LOG.debug("Remote action received by {} - {}: {}", AnkorServerBase.this, modelContext, action);

        // notify action listeners
        ListenerRegistry listenerRegistry = application.getListenerRegistry();
        Collection<ActionListener> listeners = listenerRegistry.getRemoteActionListenersFor(modelContext);
        for (ActionListener listener : listeners) {
            listener.processAction(modelContext, action);
        }
    }

    protected void receiveChange(Ref modelContext, Ref changedRef, Object newValue) {
        LOG.debug("Remote change received by {} - {}: {} (context={})", AnkorServerBase.this, changedRef, newValue,
                  modelContext);

        if (modelContext != null) {
            changedRef = changedRef.withModelContext(modelContext);
        }

        // do change model (without notifying local listeners!)
        changedRef.unwatched().setValue(newValue);

        // notify change listeners
        ListenerRegistry listenerRegistry = application.getListenerRegistry();
        Collection<BoundChangeListener> listenerInstances = listenerRegistry.getRemoteChangeListenersFor(changedRef);
        for (BoundChangeListener listenerInstance : listenerInstances) {
            ChangeListener listener = listenerInstance.getListener();
            Ref watchedRef = listenerInstance.getWatchedRef();
            listener.processChange(modelContext, watchedRef, changedRef);
        }
    }

    @Override
    public void onActionMessage(ActionMessage message) {
        receiveAction(message.getModelContext(), message.getAction());
    }

    @Override
    public void onChangeMessage(ChangeMessage message) {
        receiveChange(message.getModelContext(), message.getChange().getChangedProperty(), message.getChange().getNewValue());
    }


    private class LocalActionListener implements ActionListener {
        @Override
        public void processAction(Ref modelContext, Action action) {
            LOG.debug("Local action detected by {} - {}: {}", AnkorServerBase.this, modelContext, action);
            sendAction(modelContext, action);
        }
    }

    private void sendAction(Ref modelContext, Action action) {
        messageBus.sendMessage(messageFactory.createActionMessage(modelContext, action));
    }

    private class LocalChangeListener implements ChangeListener {
        @Override
        public void processChange(Ref modelContext, Ref watchedProperty, Ref changedProperty) {
            Object newValue = changedProperty.getValue();
            LOG.debug("Local change detected by {} - {} => {}", AnkorServerBase.this, changedProperty, newValue);
            sendChange(modelContext, changedProperty, newValue);
        }
    }

    private void sendChange(Ref contextRef, Ref changedRef, Object newValue) {
        messageBus.sendMessage(messageFactory.createChangeMessage(contextRef, changedRef, newValue));
    }

}
