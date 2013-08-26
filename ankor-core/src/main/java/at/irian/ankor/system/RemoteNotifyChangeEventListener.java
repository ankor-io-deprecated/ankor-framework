package at.irian.ankor.system;

import at.irian.ankor.change.Change;
import at.irian.ankor.change.ChangeEvent;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.change.RemoteChange;
import at.irian.ankor.context.ModelContext;
import at.irian.ankor.messaging.Message;
import at.irian.ankor.messaging.MessageFactory;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.session.Session;
import at.irian.ankor.session.SessionInitEvent;
import at.irian.ankor.session.SessionManager;

import java.util.Collection;

/**
 * Global ChangeEventListener that relays locally happened {@link ChangeEvent ChangeEvents} to all remote systems
 * connected to the underlying ModelContext.
 *
 * @author Manfred Geiler
 */
public class RemoteNotifyChangeEventListener extends ChangeEventListener implements SessionInitEvent.Listener {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RemoteNotifyChangeEventListener.class);

    private final MessageFactory messageFactory;
    private final SessionManager sessionManager;

    public RemoteNotifyChangeEventListener(MessageFactory messageFactory,
                                           SessionManager sessionManager) {
        super(null); //global listener
        this.messageFactory = messageFactory;
        this.sessionManager = sessionManager;
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
        ModelContext modelContext = changedProperty.context().modelContext();
        Collection<Session> sessions = sessionManager.getAllFor(modelContext);
        for (Session session : sessions) {
            if (change instanceof RemoteChange) {
                Session initiatingSession = ((RemoteChange) change).getSession();
                if (session.equals(initiatingSession)) {
                    // do not relay remote actions back to the remote system
                    continue;
                }
            }

            String changedPropertyPath = changedProperty.path();
            Message message = messageFactory.createChangeMessage(changedProperty.context().modelContext(),
                                                                 changedPropertyPath,
                                                                 change);
            session.getMessageSender().sendMessage(message);
        }

    }

    @Override
    public void processModelInit(SessionInitEvent event) {
        Session session = event.getSession();
        RefContext refContext = session.getRefContext();
        ModelContext modelContext = refContext.modelContext();
        Message message = messageFactory.createChangeMessage(modelContext,
                                                             refContext.refFactory().rootRef().path(),
                                                             Change.valueChange(modelContext.getModelRoot()));
        session.getMessageSender().sendMessage(message);
    }
}
