package at.irian.ankor.msg;

import at.irian.ankor.msg.party.Party;

/**
 * @author Manfred Geiler
 */
public abstract class Message {

    private final Party sender;

    protected Message(Party sender) {
        this.sender = sender;
    }

    public Party getSender() {
        return sender;
    }

    public abstract boolean isAppropriateListener(MessageListener listener);

    public abstract void processBy(MessageListener listener);

}
