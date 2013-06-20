package at.irian.ankor.service;

import at.irian.ankor.action.Action;
import at.irian.ankor.event.ActionListener;
import at.irian.ankor.application.AutoUnregisterChangeListener;
import at.irian.ankor.application.ListenerRegistry;
import at.irian.ankor.application.BoundChangeListener;
import at.irian.ankor.event.ChangeListener;
import at.irian.ankor.application.Application;
import at.irian.ankor.ref.Ref;

import java.util.Collection;

/**
 * @author MGeiler (Manfred Geiler)
 */
public abstract class AnkorServerBase {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorServerBase.class);

    protected final Application application;

    public AnkorServerBase(Application application) {
        this.application = application;
    }

    public void start() {
        LOG.debug("Starting {}", this);

        ListenerRegistry listenerRegistry = application.getListenerRegistry();

        listenerRegistry.registerLocalChangeListener(null, new LocalChangeListener());
        listenerRegistry.registerLocalActionListener(null, new LocalActionListener());

        AutoUnregisterChangeListener autoUnregisterChangeListener = new AutoUnregisterChangeListener(listenerRegistry);
        listenerRegistry.registerLocalChangeListener(null, autoUnregisterChangeListener);
        listenerRegistry.registerRemoteChangeListener(null, autoUnregisterChangeListener);
  }

    public void stop() {
        LOG.debug("Stopping {}", this);
        application.getListenerRegistry().unregisterAllListeners();
    }


    protected Application getApplication() {
        return application;
    }

    protected void receiveAction(Ref contextRef, Action action) {
        LOG.debug("Remote action received by {} - {}: {}", AnkorServerBase.this, contextRef, action);

        // notify action listeners
        ListenerRegistry listenerRegistry = application.getListenerRegistry();
        Collection<ActionListener> listeners = listenerRegistry.getRemoteActionListenersFor(contextRef);
        for (ActionListener listener : listeners) {
            listener.processAction(contextRef, action);
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




    private class LocalActionListener implements ActionListener {
        @Override
        public void processAction(Ref modelContext, Action action) {
            LOG.debug("Local action detected by {} - {}: {}", AnkorServerBase.this, modelContext, action);
            sendAction(modelContext, action);
        }
    }

    protected abstract void sendAction(Ref contextRef, Action action);


    private class LocalChangeListener implements ChangeListener {
        @Override
        public void processChange(Ref modelContext, Ref watchedProperty, Ref changedProperty) {
            Object newValue = changedProperty.getValue();
            LOG.debug("Local change detected by {} - {} => {}", AnkorServerBase.this, changedProperty, newValue);
            sendChange(modelContext, changedProperty, newValue);
        }
    }

    protected abstract void sendChange(Ref contextRef, Ref changedRef, Object newValue);


}
