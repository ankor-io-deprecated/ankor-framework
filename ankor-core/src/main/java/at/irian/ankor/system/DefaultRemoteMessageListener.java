package at.irian.ankor.system;

import at.irian.ankor.action.Action;
import at.irian.ankor.change.Change;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.session.ModelSessionManager;
import at.irian.ankor.messaging.ActionMessage;
import at.irian.ankor.messaging.ChangeMessage;
import at.irian.ankor.messaging.Message;
import at.irian.ankor.messaging.RemoteMessageListener;
import at.irian.ankor.messaging.modify.Modifier;
import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.impl.RefImplementor;
import at.irian.ankor.connection.*;

/**
 * Main system message listener that propagates remote events (actions and changes)
 * to the local {@linkplain AnkorSystem Ankor system's}
 * {@link at.irian.ankor.event.dispatch.EventDispatcher EventDispatcher}.
 *
* @author Manfred Geiler
*/
public class DefaultRemoteMessageListener implements RemoteMessageListener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultRemoteMessageListener.class);

    private final ModelSessionManager modelSessionManager;
    private final ModelConnectionManager modelConnectionManager;
    private final ModelRootFactory modelRootFactory;
    private final Modifier modifier;

    DefaultRemoteMessageListener(ModelSessionManager modelSessionManager,
                                 ModelConnectionManager modelConnectionManager,
                                 ModelRootFactory modelRootFactory,
                                 Modifier modifier) {
        this.modelSessionManager = modelSessionManager;
        this.modelConnectionManager = modelConnectionManager;
        this.modelRootFactory = modelRootFactory;
        this.modifier = modifier;
    }

    @Override
    public void onActionMessage(ActionMessage message) {
        LOG.debug("received {}", message);

        if (message.getProperty() == null) {
            // ignore global actions
            return;
        }

        ModelSession modelSession = modelSessionManager.getOrCreate(message.getModelId());
        final ModelConnection modelConnection = modelConnectionManager.getOrCreate(modelSession, getRemoteSystemOf(message));
        final Ref actionProperty = modelConnection.getRefContext().refFactory().ref(message.getProperty());

        if (actionProperty.isRoot() && actionProperty.getValue() == null) {
            // this model root does not yet exist
            // this is most probably a bootstrap init action...
            final Object modelRoot = modelRootFactory.createModelRoot(actionProperty);

            // we must not directly access the model session from a non-dispatching thread
            AnkorPatterns.runLater(actionProperty, new Runnable() {
                @Override
                public void run() {
                    actionProperty.setValue(modelRoot);
                }
            });
        }

        final Action action = modifier.modifyAfterReceive(message.getAction(), actionProperty);

        AnkorPatterns.runLater(actionProperty, new Runnable() {
            @Override
            public void run() {
                ((RefImplementor)actionProperty).fire(new RemoteSource(modelConnection), action);
            }
        });
    }

    @Override
    public void onChangeMessage(ChangeMessage message) {
        LOG.debug("received {}", message);

        ModelSession modelSession = modelSessionManager.getOrCreate(message.getModelId());
        final ModelConnection modelConnection = modelConnectionManager.getOrCreate(modelSession, getRemoteSystemOf(message));
        final Ref changedProperty = modelConnection.getRefContext().refFactory().ref(message.getProperty());
        final Change change = modifier.modifyAfterReceive(message.getChange(), changedProperty);

        AnkorPatterns.runLater(changedProperty, new Runnable() {
            @Override
            public void run() {
                ((RefImplementor)changedProperty).apply(new RemoteSource(modelConnection), change);
            }
        });
    }


    private RemoteSystem getRemoteSystemOf(Message message) {
        return new SimpleRemoteSystem(message.getSenderId());
    }


}
