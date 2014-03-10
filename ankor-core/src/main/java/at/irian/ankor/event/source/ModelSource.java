package at.irian.ankor.event.source;

import at.irian.ankor.switching.connector.local.LocalModelAddress;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.session.ModelSession;

/**
 * Local source of a model event.
 *
 * @author Manfred Geiler
 */
public class ModelSource extends ModelAddressSource {

    private final ModelSession modelSession;

    public ModelSource(ModelSession modelSession, String modelName, Object origination) {
        super(new LocalModelAddress(modelSession.getId(), modelName), origination);
        this.modelSession = modelSession;
    }

    public ModelSource(Ref ref, Object origination) {
        this(ref.context().modelSession(), ref.root().propertyName(), origination);
    }

    public ModelSession getModelSession() {
        return modelSession;
    }

    @Override
    public String toString() {
        return "ModelSource{" +
               "address=" + getModelAddress() +
               ", origination=" + getOrigination() +
               ", modelSession=" + modelSession +
               "}";
    }
}
