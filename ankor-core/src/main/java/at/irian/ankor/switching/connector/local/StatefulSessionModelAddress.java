package at.irian.ankor.switching.connector.local;

import at.irian.ankor.session.ModelSession;
import at.irian.ankor.switching.routing.ModelAddress;

import java.io.Serializable;

/**
 * @author Manfred Geiler
 */
public class StatefulSessionModelAddress implements ModelAddress, Serializable {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(StatefulSessionModelAddress.class);

    private final String modelSessionId;
    private final String modelName;
    private final int hashCode;
    private final String consistentHashKey;

    public StatefulSessionModelAddress(ModelSession modelSession, String modelName) {
        this(modelSession.getId(), modelName);
    }

    public StatefulSessionModelAddress(String modelSessionId, String modelName) {
        this.modelSessionId = modelSessionId;
        this.modelName = modelName;
        this.hashCode = 31 * modelSessionId.hashCode() + modelName.hashCode();
        this.consistentHashKey = modelSessionId + modelName;
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

        StatefulSessionModelAddress that = (StatefulSessionModelAddress) o;

        return modelName.equals(that.modelName) && modelSessionId.equals(that.modelSessionId);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String consistentHashKey() {
        return consistentHashKey;
    }

    @Override
    public String toString() {
        return "StatefulSessionModelAddress{" +
               "modelSessionId='" + modelSessionId + '\'' +
               ", modelName='" + modelName + '\'' +
               '}';
    }


}
