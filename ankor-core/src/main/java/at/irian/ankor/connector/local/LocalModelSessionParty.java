package at.irian.ankor.connector.local;

import at.irian.ankor.msg.party.Party;

/**
 * @author Manfred Geiler
 */
public class LocalModelSessionParty implements Party {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LocalModelSessionParty.class);

    private final String modelSessionId;

    public LocalModelSessionParty(String modelSessionId) {
        this.modelSessionId = modelSessionId;
    }

    public String getModelSessionId() {
        return modelSessionId;
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

        return modelSessionId.equals(that.modelSessionId);
    }

    @Override
    public int hashCode() {
        return modelSessionId.hashCode();
    }

    @Override
    public String toString() {
        return "LocalModelSessionParty{" +
               "modelSessionId='" + modelSessionId + '\'' +
               '}';
    }

}
