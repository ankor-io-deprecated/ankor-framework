package at.irian.ankor.event.source;

import at.irian.ankor.switching.routing.ModelAddress;

/**
 * Event source for an event that is derived from an incoming message from a remote modelAddress.
 * A ModelAddressSource is always associated to a corresponding ModelAddress.
 *
 * @author Manfred Geiler
 */
public class ModelAddressSource implements Source {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelAddressSource.class);

    private final ModelAddress modelAddress;
    private final Object origination;

    public ModelAddressSource(ModelAddress modelAddress, Object origination) {
        this.modelAddress = modelAddress;
        this.origination = origination;
    }

    public ModelAddress getModelAddress() {
        return modelAddress;
    }

    public Object getOrigination() {
        return origination;
    }

    @Override
    public String toString() {
        return "ModelAddressSource{" +
               "modelAddress=" + modelAddress +
               ", origination=" + origination +
               '}';
    }
}
