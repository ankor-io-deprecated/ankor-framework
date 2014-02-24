package at.irian.ankor.messaging;

import at.irian.ankor.action.Action;
import at.irian.ankor.change.Change;
import at.irian.ankor.session.ModelSession;

/**
 * @author Manfred Geiler
 */
public class MessageFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MessageFactory.class);

    private final String systemName;
    private final MessageIdGenerator messageIdGenerator;

    public MessageFactory(String systemName, MessageIdGenerator messageIdGenerator) {
        this.systemName = systemName;
        this.messageIdGenerator = messageIdGenerator;
    }

    public Message createActionMessage(ModelSession modelSession, String actionPropertyPath, Action action) {
        return new ActionMessage(systemName, modelSession.getId(), messageIdGenerator.create(), actionPropertyPath, action);
    }

    public Message createChangeMessage(ModelSession modelSession, String changedPropertyPath, Change change) {
        return new ChangeMessage(systemName, modelSession.getId(), messageIdGenerator.create(), changedPropertyPath, change);
    }

    public Message createGlobalActionMessage(Action action) {
        return new ActionMessage(systemName, null, messageIdGenerator.create(), null, action);
    }

}
