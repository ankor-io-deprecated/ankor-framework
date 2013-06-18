package at.irian.ankor.sample.fx.model;

/**
* @author Thomas Spiegl
*/
public class Animal {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Animal.class);

    private final String name;
    private final AnimalType type;

    public Animal(String name, AnimalType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public AnimalType getType() {
        return type;
    }
}
