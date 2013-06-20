package at.irian.ankor.service.test.animal;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class AnimalFilter {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalFilter.class);

    private String name;
    private AnimalType type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AnimalType getType() {
        return type;
    }

    public void setType(AnimalType type) {
        this.type = type;
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        AnimalFilter that = (AnimalFilter) o;

        if (name != null ? !name.equals(that.name) : that.name != null) { return false; }
        if (type != that.type) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
