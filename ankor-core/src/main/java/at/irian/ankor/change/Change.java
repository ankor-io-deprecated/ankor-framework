package at.irian.ankor.change;

/**
 * @author Manfred Geiler
 */
public class Change {

    private Object newValue;

    /**
     * for deserialization only
     */
    protected Change() {}

    public Change(Object newValue) {
        this.newValue = newValue;
    }

    public Object getNewValue() {
        return newValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Change change = (Change) o;

        return !(newValue != null ? !newValue.equals(change.newValue) : change.newValue != null);
    }

    @Override
    public int hashCode() {
        return newValue != null ? newValue.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Change{" +
               "newValue=" + newValue +
               '}';
    }


}
