package at.irian.ankor.api.model.deprecated.generic;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ModelValue extends ModelObject {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelValue.class);

    public static final ModelValue NULL = new ModelValue(null);

    private final Object value;

    public ModelValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public boolean isNull() {
        return value == null;
    }

    @Override
    public String toString() {
        return value != null ? value.toString() : "null";
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        ModelValue that = (ModelValue) o;

        if (value != null ? !value.equals(that.value) : that.value != null) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
