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

        @SuppressWarnings("RedundantIfStatement")
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Change change = (Change) o;

            if (!changedProperty.equals(change.changedProperty)) {
                return false;
            }
            if (newValue != null ? !newValue.equals(change.newValue) : change.newValue != null) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = changedProperty.hashCode();
            result = 31 * result + (newValue != null ? newValue.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Change{" +
                   "changedProperty=" + changedProperty +
                   ", newValue=" + newValue +
                   "}";
        }
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

        ChangeMessage that = (ChangeMessage) o;

        if (!change.equals(that.change)) {
            return false;
        }
        if (modelContextPath != null
            ? !modelContextPath.equals(that.modelContextPath)
            : that.modelContextPath != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = modelContextPath != null ? modelContextPath.hashCode() : 0;
        result = 31 * result + change.hashCode();
        return result;
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
