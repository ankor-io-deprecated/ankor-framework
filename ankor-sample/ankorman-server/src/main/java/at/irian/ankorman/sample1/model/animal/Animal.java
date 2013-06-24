package at.irian.ankorman.sample1.model.animal;

import java.util.UUID;

/**
* @author Thomas Spiegl
*/
public class Animal {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Animal.class);

    private final String uuid;
    private String name;
    private AnimalType type;
    private AnimalFamily family;

    public Animal() {
        this.uuid = UUID.randomUUID().toString();
    }

    public Animal(Animal other) {
        this.uuid = other.uuid;
        this.name = other.name;
        this.type = other.type;
        this.family = other.family;
    }

    public Animal(String name, AnimalType type, AnimalFamily family) {
        this.uuid = UUID.randomUUID().toString();
        this.name = name;
        this.type = type;
        this.family = family;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public AnimalType getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(AnimalType type) {
        this.type = type;
    }

    public AnimalFamily getFamily() {
        return family;
    }

    public void setFamily(AnimalFamily family) {
        this.family = family;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Animal{");
        sb.append("name='").append(name).append('\'');
        sb.append(", type=").append(type);
        sb.append(", family=").append(family);
        sb.append('}');
        return sb.toString();
    }
}
