package at.irian.ankor.connector.local;

import at.irian.ankor.msg.party.Party;

/**
 * @author Manfred Geiler
 */
public class LocalModelSessionParty implements Party {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LocalModelSessionParty.class);

    private final String modelSessionId;
    private final String modelName;

    public LocalModelSessionParty(String modelSessionId, String modelName) {
        this.modelSessionId = modelSessionId;
        this.modelName = modelName;
    }

    public String getModelSessionId() {
        return modelSessionId;
    }

    @Override
    public String getModelName() {
        return modelName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LocalModelSessionParty that = (LocalModelSessionParty) o;

        return modelName.equals(that.modelName) && modelSessionId.equals(that.modelSessionId);

    }

    @Override
    public int hashCode() {
        int result = modelSessionId.hashCode();
        result = 31 * result + modelName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "LocalModelSessionParty{" +
               "modelSessionId='" + modelSessionId + '\'' +
               ", modelName='" + modelName + '\'' +
               '}';
    }


}
