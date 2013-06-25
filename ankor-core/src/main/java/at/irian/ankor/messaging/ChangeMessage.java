package at.irian.ankor.messaging;

/**
 * @author Manfred Geiler
 */
public class ChangeMessage extends Message {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ChangeMessage.class);

    private String modelContextPath;
    private Change change;

    /**
     * for deserialization only
     */
    @SuppressWarnings("UnusedDeclaration")
    protected ChangeMessage() {}

    protected ChangeMessage(String messageId, String modelContextPath, String changedPropertyPath, Object newValue) {
        super(messageId);
        this.modelContextPath = modelContextPath;
        this.change = new Change(changedPropertyPath, newValue);
    }

    public String getModelContextPath() {
        return modelContextPath;
    }

    public Change getChange() {
        return change;
    }

    public static class Change {
        private String changedProperty;
        private Object newValue;

        @SuppressWarnings("UnusedDeclaration")
        protected Change() {}

        public Change(String changedProperty, Object newValue) {
            this.changedProperty = changedProperty;
            this.newValue = newValue;
        }

        public String getChangedProperty() {
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
               ", modelContextPath=" + modelContextPath +
               ", change=" + change +
               "}";
    }
}
