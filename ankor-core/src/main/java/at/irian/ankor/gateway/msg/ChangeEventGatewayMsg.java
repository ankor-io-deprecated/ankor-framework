package at.irian.ankor.gateway.msg;

import at.irian.ankor.change.Change;

/**
 * @author Manfred Geiler
 */
public class ChangeEventGatewayMsg extends GatewayMsg {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ChangeMessage.class);

    private String property;
    private Change change;

    public ChangeEventGatewayMsg(String property, Change change) {
        super();
        this.property = property;
        this.change = change;
    }

    public String getProperty() {
        return property;
    }

    public Change getChange() {
        return change;
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

        ChangeEventGatewayMsg that = (ChangeEventGatewayMsg) o;

        if (!change.equals(that.change)) {
            return false;
        }
        if (!property.equals(that.property)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = property.hashCode();
        result = 31 * result + change.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ChangeEventMessage{" +
               "property='" + property + '\'' +
               ", change=" + change +
               "}";
    }

}
