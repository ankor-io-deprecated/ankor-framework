package at.irian.ankor.session;

import at.irian.ankor.context.ModelContext;
import at.irian.ankor.messaging.MessageSender;
import at.irian.ankor.ref.RefContext;

/**
 * @author Manfred Geiler
 */
public interface Session {

    String getId();

    void invalidate();

    ModelContext getModelContext();

    RefContext getRefContext();

    MessageSender getMessageSender();

    void setMessageSender(MessageSender messageSender);
}
