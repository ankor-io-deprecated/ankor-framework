package at.irian.ankor.messaging;

import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public class ChangeMessage extends Message {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ChangeMessage.class);

    private Change change;

    protected ChangeMessage() {
    }

    public ChangeMessage(String messageId, Ref modelContext, Ref changedProperty, Object newValue) {
        super(messageId);
        this.change = new Change(modelContext, changedProperty, newValue);
    }

    public Change getChange() {
        return change;
    }

    public static class Change {
        private Ref modelContext;
        private Ref changedProperty;
        private Object newValue;

        Change() {}

        Change(Ref modelContext, Ref changedProperty, Object newValue) {
            this.modelContext = modelContext;
            this.changedProperty = changedProperty;
            this.newValue = newValue;
        }

        public Ref getModelContext() {
            return modelContext;
        }

        public Ref getChangedProperty() {
            return changedProperty;
        }

        public Object getNewValue() {
            return newValue;
        }
    }

}
