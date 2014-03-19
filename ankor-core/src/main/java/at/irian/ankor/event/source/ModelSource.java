package at.irian.ankor.event.source;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.switching.connector.local.LocalModelAddress;

/**
 * Local source of a model event.
 *
 * @author Manfred Geiler
 */
public class ModelSource extends ModelAddressSource {

    public ModelSource(String modelSessionId, String modelName, Object origination) {
        super(new LocalModelAddress(modelSessionId, modelName), origination);
    }

    public static ModelSource createFrom(Ref modelProperty, Object origination) {
        return new ModelSource(modelProperty.context().modelSession().getId(),
                               modelProperty.root().propertyName(),
                               origination);
    }

    public String getModelSessionId() {
        return ((LocalModelAddress)getModelAddress()).getModelSessionId();
    }

    @Override
    public String toString() {
        return "ModelSource{" +
               "address=" + getModelAddress() +
               ", origination=" + getOrigination() +
               ", modelSessionId=" + getModelSessionId() +
               "}";
    }
}
