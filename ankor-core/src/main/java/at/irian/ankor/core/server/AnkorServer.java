package at.irian.ankor.core.server;

import at.irian.ankor.core.listener.ModelActionListener;
import at.irian.ankor.core.listener.ModelChangeListener;
import at.irian.ankor.core.application.Application;
import at.irian.ankor.core.ref.ModelRef;

import java.util.*;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class AnkorServer {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorServer.class);

    private final Application application;

    public AnkorServer(Application application) {
        this.application = application;
        this.application.getListenerRegistry().registerLocalChangeListener(null,
                                                                           new ClientNotifyingChangeListener(this));
        this.application.getListenerRegistry().registerLocalActionListener(null,
                                                                           new ClientNotifyingActionListener(this));
    }

    public void handleClientAction(String path, String action) {
        ModelRef modelRef = application.getRefFactory().ref(path);
        Collection<ModelActionListener> listeners = application.getListenerRegistry().getRemoteActionListenersFor(modelRef);
        for (ModelActionListener listener : listeners) {
            listener.handleModelAction(modelRef, action);
        }
    }

    public void handleClientChange(String path, Object newValue) {
        ModelRef modelRef = application.getRefFactory().ref(path);
        Object oldValue = modelRef.getValue();
        Collection<ModelChangeListener> listeners
                = application.getListenerRegistry().getRemoteChangeListenersFor(modelRef);

        for (ModelChangeListener listener : listeners) {
            listener.beforeModelChange(modelRef, oldValue, newValue);
        }

        modelRef.unwatched().setValue(newValue);

        for (ModelChangeListener listener : listeners) {
            listener.afterModelChange(modelRef, oldValue, newValue);
        }
    }

    protected void handleServerChange(ModelRef modelRef, Object oldValue, Object newValue) {
        LOG.info("Server model changed: ref={}, old={}, new={}", modelRef, oldValue, newValue);
    }

    public void handleServerAction(ModelRef actionContext, String action) {
        LOG.info("Server model action:  ref={}, action={}", actionContext, action);
    }
}
