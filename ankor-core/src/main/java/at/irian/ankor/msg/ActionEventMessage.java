package at.irian.ankor.msg;

import at.irian.ankor.action.Action;
import at.irian.ankor.event.source.Source;
import at.irian.ankor.msg.party.Party;

/**
 * @author Manfred Geiler
 */
public class ActionEventMessage extends EventMessage {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ActionEventMessage.class);

    private String property;
    private Action action;

    public ActionEventMessage(Party sender, Source eventSource, String property, Action action) {
        super(sender, eventSource);
        this.property = property;
        this.action = action;
    }

    @Override
    public EventMessage withSender(Party sender) {
        return new ActionEventMessage(sender, getEventSource(), getProperty(), getAction());
    }

    public String getProperty() {
        return property;
    }

    public Action getAction() {
        return action;
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ActionEventMessage that = (ActionEventMessage) o;

        if (action != null ? !action.equals(that.action) : that.action != null) {
            return false;
        }
        if (property != null ? !property.equals(that.property) : that.property != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = property != null ? property.hashCode() : 0;
        result = 31 * result + (action != null ? action.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ActionEventMessage{" +
               "sender='" + getSender() + '\'' +
               ", property='" + property + '\'' +
               ", action=" + action +
               "}";
    }


}
