package at.irian.ankor.viewmodel.metadata;

/**
 * @author Manfred Geiler
 */
public class VirtualMetadata {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(VirtualMetadata.class);

    private final boolean virtual;
    private final Class<?> propertyType;

    public VirtualMetadata(boolean virtual, Class<?> propertyType) {
        this.virtual = virtual;
        this.propertyType = propertyType;
    }

    public boolean isVirtual() {
        return virtual;
    }

    public Class<?> getPropertyType() {
        return propertyType;
    }
}

