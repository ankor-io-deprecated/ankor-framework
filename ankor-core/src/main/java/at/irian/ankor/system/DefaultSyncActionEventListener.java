package at.irian.ankor.system;

import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.messaging.Message;
import at.irian.ankor.messaging.MessageFactory;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.session.Session;

/**
 * @author Manfred Geiler
 */
public class DefaultSyncActionEventListener extends ActionEvent.Listener {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultSyncActionEventListener.class);

    private final MessageFactory messageFactory;
    private final Session session;

    public DefaultSyncActionEventListener(MessageFactory messageFactory,
                                          Session session) {
        super(null); //global listener
        this.messageFactory = messageFactory;
        this.session = session;
    }


    @Override
    public void process(ActionEvent event) {
        Ref actionProperty = event.getActionProperty();
        String actionPropertyPath = actionProperty.path();
        Message message = messageFactory.createActionMessage(session.getId(),
                                                             actionPropertyPath,
                                                             event.getAction());
        session.getMessageSender().sendMessage(message);
    }
}
