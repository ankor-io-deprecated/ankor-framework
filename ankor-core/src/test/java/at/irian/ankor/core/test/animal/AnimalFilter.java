package at.irian.ankor.core.test.animal;

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
}
