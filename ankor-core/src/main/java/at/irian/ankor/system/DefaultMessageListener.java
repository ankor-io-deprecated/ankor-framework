package at.irian.ankor.system;

import at.irian.ankor.action.Action;
import at.irian.ankor.context.ModelContext;
import at.irian.ankor.context.ModelContextManager;
import at.irian.ankor.messaging.ActionMessage;
import at.irian.ankor.messaging.ChangeMessage;
import at.irian.ankor.messaging.Message;
import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.session.*;

import static at.irian.ankor.system.RemoteEvent.createActionEvent;
import static at.irian.ankor.system.RemoteEvent.createChangeEvent;

/**
 * Main system message listener that propagates remote events (actions and changes)
 * to the local {@linkplain AnkorSystem Ankor system's}
 * {@link at.irian.ankor.event.dispatch.EventDispatcher EventDispatcher}.
 *
* @author Manfred Geiler
*/
public class DefaultMessageListener implements ActionMessage.Listener, ChangeMessage.Listener {

    private final ModelContextManager modelContextManager;
    private final SessionManager sessionManager;
    private final ModelRootFactory modelRootFactory;

    DefaultMessageListener(ModelContextManager modelContextManager,
                           SessionManager sessionManager,
                           ModelRootFactory modelRootFactory) {
        this.modelContextManager = modelContextManager;
        this.sessionManager = sessionManager;
        this.modelRootFactory = modelRootFactory;
    }

    @Override
    public void onActionMessage(ActionMessage message) {

        if (message.getProperty() == null) {
            // ignore global actions
            return;
        }

        ModelContext modelContext = modelContextManager.getOrCreate(message.getModelId());
        Session session = sessionManager.getOrCreate(modelContext, getRemoteSystemOf(message));

        RefContext refContext = session.getRefContext();
        Ref actionProperty = refContext.refFactory().ref(message.getProperty());

        if (actionProperty.isRoot() && actionProperty.getValue() == null) {
            // this model root does not yet exist
            Object modelRoot = modelRootFactory.createModelRoot(actionProperty);
            AnkorPatterns.changeValueLater(actionProperty, modelRoot);
        }

        Action action = message.getAction();
        RemoteEvent event = createActionEvent(session, actionProperty, action.getName(), action.getParams());
        modelContext.getEventDispatcher().dispatch(event);
    }

    @Override
    public void onChangeMessage(ChangeMessage message) {
        ModelContext modelContext = modelContextManager.getOrCreate(message.getModelId());
        Session session = sessionManager.getOrCreate(modelContext, getRemoteSystemOf(message));

        RefContext refContext = session.getRefContext();
        Ref changedProperty = refContext.refFactory().ref(message.getProperty());

        RemoteEvent event = createChangeEvent(session, changedProperty, message.getChange());
        modelContext.getEventDispatcher().dispatch(event);
    }


    private RemoteSystem getRemoteSystemOf(Message message) {
        return new SimpleRemoteSystem(message.getSenderId());
    }


}
