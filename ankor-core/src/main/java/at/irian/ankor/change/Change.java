package at.irian.ankor.change;

/**
 * @author Manfred Geiler
 */
public class Change {

    private ChangeType type;
    private Object key;
    private Object value;

    /**
     * for deserialization only
     */
    protected Change() {}

    protected Change(ChangeType type, Object key, Object value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }


    public static Change valueChange(Object newValue) {
        return new Change(ChangeType.new_value, null, newValue);
    }

    public static Change insertChange(int idx, Object newValue) {
        return new Change(ChangeType.insert, idx, newValue);
    }

    public static Change deleteChange(Object key) {
        return new Change(ChangeType.delete, key, null);
    }


    public ChangeType getType() {
        return type;
    }

    public Object getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Change)) {
            return false;
        }

        Change change = (Change) o;

        if (key != null ? !key.equals(change.key) : change.key != null) {
            return false;
        }
        if (type != change.type) {
            return false;
        }
        if (value != null ? !value.equals(change.value) : change.value != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + (key != null ? key.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Change{" +
               "type=" + type +
               ", key=" + key +
               ", value=" + value +
               "}";
    }


}
