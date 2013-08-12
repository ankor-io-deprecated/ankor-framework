package at.irian.ankor.messaging;

import at.irian.ankor.action.Action;
import at.irian.ankor.change.Change;
import at.irian.ankor.context.ModelContext;

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

    public Message createActionMessage(ModelContext modelContext, String actionPropertyPath, Action action) {
        return new ActionMessage(systemName, modelContext.getId(), messageIdGenerator.create(), actionPropertyPath, action);
    }

    public Message createChangeMessage(ModelContext modelContext, String changedPropertyPath, Change change) {
        return new ChangeMessage(systemName, modelContext.getId(), messageIdGenerator.create(), changedPropertyPath, change);
    }

    public Message createGlobalActionMessage(Action action) {
        return new ActionMessage(systemName, null, messageIdGenerator.create(), null, action);
    }

}
