package at.irian.ankor.switching.connector.local;

import at.irian.ankor.session.ModelSession;
import at.irian.ankor.switching.routing.ModelAddress;

/**
 * @author Manfred Geiler
 */
public class LocalModelAddress implements ModelAddress {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LocalModelAddress.class);

    private final String modelSessionId;
    private final String modelName;
    private final int hashCode;

    public LocalModelAddress(ModelSession modelSession, String modelName) {
        this(modelSession.getId(), modelName);
    }

    public LocalModelAddress(String modelSessionId, String modelName) {
        this.modelSessionId = modelSessionId;
        this.modelName = modelName;
        this.hashCode = 31 * modelSessionId.hashCode() + modelName.hashCode();
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

        LocalModelAddress that = (LocalModelAddress) o;

        return modelName.equals(that.modelName) && modelSessionId.equals(that.modelSessionId);

    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return "LocalModelAddress{" +
               "modelSessionId='" + modelSessionId + '\'' +
               ", modelName='" + modelName + '\'' +
               '}';
    }


}
