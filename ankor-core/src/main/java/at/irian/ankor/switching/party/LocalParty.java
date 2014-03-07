package at.irian.ankor.switching.party;

import at.irian.ankor.session.ModelSession;

/**
 * @author Manfred Geiler
 */
public class LocalParty implements Party {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LocalParty.class);

    private final String modelSessionId;
    private final String modelName;

    public LocalParty(ModelSession modelSession, String modelName) {
        this(modelSession.getId(), modelName);
    }

    public LocalParty(String modelSessionId, String modelName) {
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

        LocalParty that = (LocalParty) o;

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
        return "LocalParty{" +
               "modelSessionId='" + modelSessionId + '\'' +
               ", modelName='" + modelName + '\'' +
               '}';
    }


}
