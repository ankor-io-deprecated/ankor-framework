package at.irian.ankor.system;

import at.irian.ankor.action.Action;
import at.irian.ankor.context.ModelContext;
import at.irian.ankor.context.ModelContextManager;
import at.irian.ankor.messaging.ActionMessage;
import at.irian.ankor.messaging.ChangeMessage;
import at.irian.ankor.messaging.MessageListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.session.Session;
import at.irian.ankor.session.SessionManager;

import static at.irian.ankor.system.RemoteEvent.createActionEvent;
import static at.irian.ankor.system.RemoteEvent.createChangeEvent;

/**
 * Main system message listener that propagates remote events (actions and changes)
 * to the local {@linkplain AnkorSystem Ankor system's}
 * {@link at.irian.ankor.event.dispatch.EventDispatcher EventDispatcher}.
 *
* @author Manfred Geiler
*/
public class DefaultMessageListener implements MessageListener {

    private final ModelContextManager modelContextManager;
    private final SessionManager sessionManager;

    DefaultMessageListener(ModelContextManager modelContextManager, SessionManager sessionManager) {
        this.modelContextManager = modelContextManager;
        this.sessionManager = sessionManager;
    }

    @Override
    public void onActionMessage(ActionMessage message) {
        ModelContext modelContext = modelContextManager.getOrCreate(message.getModelId());
        Session session = sessionManager.getOrCreate(modelContext, message.getSenderId());

        RefContext refContext = session.getRefContext();
        Ref actionProperty = refContext.refFactory().ref(message.getProperty());

        Action action = message.getAction();
        RemoteEvent event = createActionEvent(actionProperty, action.getName(), action.getParams());
        modelContext.getEventDispatcher().dispatch(event);
    }

    @Override
    public void onChangeMessage(ChangeMessage message) {
        ModelContext modelContext = modelContextManager.getOrCreate(message.getModelId());
        Session session = sessionManager.getOrCreate(modelContext, message.getSenderId());

        RefContext refContext = session.getRefContext();
        Ref changedProperty = refContext.refFactory().ref(message.getProperty());

        RemoteEvent event = createChangeEvent(changedProperty, message.getChange().getNewValue());
        modelContext.getEventDispatcher().dispatch(event);
    }
}
