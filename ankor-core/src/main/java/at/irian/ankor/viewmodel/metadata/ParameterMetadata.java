package at.irian.ankor.viewmodel.metadata;

/**
 * @author Manfred Geiler
 */
public class ParameterMetadata {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ParameterMetadata.class);

    private final String name;
    private final boolean backReference;

    public ParameterMetadata(String name, boolean backReference) {
        this.name = name;
        this.backReference = backReference;
    }

    public String getName() {
        return name;
    }

    public boolean isBackReference() {
        return backReference;
    }
}
