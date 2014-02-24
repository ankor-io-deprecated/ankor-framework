package at.irian.ankor.system;

import at.irian.ankor.change.Change;
import at.irian.ankor.change.ChangeEvent;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.messaging.Message;
import at.irian.ankor.messaging.MessageFactory;
import at.irian.ankor.messaging.modify.Modifier;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.connection.ModelConnectionInitEvent;
import at.irian.ankor.connection.ModelConnectionManager;
import at.irian.ankor.connection.ModelRootFactory;
import at.irian.ankor.connection.ModelConnection;

import java.util.Collection;
import java.util.Set;

/**
 * Global ChangeEventListener that relays locally happened {@link ChangeEvent ChangeEvents} to all remote systems
 * connected to the underlying ModelSession.
 *
 * @author Manfred Geiler
 */
public class RemoteNotifyChangeEventListener extends ChangeEventListener implements ModelConnectionInitEvent.Listener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RemoteNotifyChangeEventListener.class);

    private final MessageFactory messageFactory;
    private final ModelConnectionManager modelConnectionManager;
    private final Modifier preSendModifier;
    private Set<String> rootNames;

    public RemoteNotifyChangeEventListener(MessageFactory messageFactory,
                                           ModelConnectionManager modelConnectionManager,
                                           ModelRootFactory modelRootFactory,
                                           Modifier preSendModifier) {
        super(null); //global listener
        this.messageFactory = messageFactory;
        this.modelConnectionManager = modelConnectionManager;
        this.preSendModifier = preSendModifier;
        this.rootNames = modelRootFactory.getKnownRootNames();
    }

    @Override
    public boolean isDiscardable() {
        return false; // this is a global system listener
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void process(ChangeEvent event) {
        Change change = event.getChange();
        Ref changedProperty = event.getChangedProperty();
        Change modifiedChange = preSendModifier.modifyBeforeSend(change, changedProperty);
        ModelSession modelSession = changedProperty.context().modelSession();
        Collection<ModelConnection> modelConnections = modelConnectionManager.getAllFor(modelSession);
        for (ModelConnection modelConnection : modelConnections) {
            if (event.getSource() instanceof RemoteSource) {
                ModelConnection initiatingModelConnection = ((RemoteSource)event.getSource()).getModelConnection();
                if (modelConnection.equals(initiatingModelConnection)) {
                    // do not relay remote actions back to the remote system
                    continue;
                }
            }

            String changedPropertyPath = changedProperty.path();
            Message message = messageFactory.createChangeMessage(changedProperty.context().modelSession(),
                                                                 changedPropertyPath,
                                                                 modifiedChange);
            LOG.debug("server sends {}", message);
            modelConnection.getMessageSender().sendMessage(message);
        }

    }

    @Override
    public void processModelConnectionInit(ModelConnectionInitEvent event) {
        ModelConnection modelConnection = event.getModelConnection();
        RefContext refContext = modelConnection.getRefContext();
        ModelSession modelSession = refContext.modelSession();

        for (String rootName : rootNames) {
            Ref rootRef = refContext.refFactory().ref(rootName);
            Object rootObj = rootRef.getValue();
            if (rootObj != null) {
                Change change = Change.valueChange(rootObj);
                Change modifiedChange = preSendModifier.modifyBeforeSend(change, rootRef);
                Message message = messageFactory.createChangeMessage(modelSession,
                                                                     rootRef.path(),
                                                                     modifiedChange);
                LOG.debug("server sends {}", message);
                modelConnection.getMessageSender().sendMessage(message);
            }
        }

    }
}
