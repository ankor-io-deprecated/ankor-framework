package at.irian.ankor.messaging;

import at.irian.ankor.ref.Ref;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Manfred Geiler
 */
public class ChangeMessage extends Message {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ChangeMessage.class);

    private Ref modelContext;
    private Change change;

    /**
     * for deserialization only
     */
    protected ChangeMessage() {}

    protected ChangeMessage(String messageId, Ref modelContext, Ref changedProperty, Object newValue) {
        super(messageId);
        this.modelContext = modelContext;
        this.change = new Change(changedProperty, newValue);
    }

    public Ref getModelContext() {
        return modelContext;
    }

    public Change getChange() {
        return change;
    }

    public static class Change {
        private Ref changedProperty;
        private Object newValue;

        Change() {}

        public Change(Ref changedProperty, Object newValue) {
            this.changedProperty = changedProperty;
            this.newValue = newValue;
        }

        public Ref getChangedProperty() {
            return changedProperty;
        }

        public Object getNewValue() {
            return newValue;
        }

        @Override
        public String toString() {
            return "Change{" +
                   "changedProperty=" + changedProperty +
                   ", newValue=" + newValue +
                   "}";
        }
    }

    @Override
    public String toString() {
        return "ChangeMessage{" +
               "messageId='" + getMessageId() + '\'' +
               ", modelContext=" + modelContext +
               ", change=" + change +
               "}";
    }
}
