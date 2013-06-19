package at.irian.ankor.sample.fx.server.model;

import java.util.UUID;

/**
* @author Thomas Spiegl
*/
public class Animal {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Animal.class);

    private final String uuid;
    private String name;
    private AnimalType type;

    public Animal(Animal other) {
        this.uuid = other.uuid;
        this.name = other.name;
        this.type = other.type;
    }

    public Animal(String name, AnimalType type) {
        this.uuid = UUID.randomUUID().toString();
        this.name = name;
        this.type = type;
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
}
